package com.org.example.service;

import com.org.example.dto.LearningJourneyResponse;
import com.org.example.dto.LearningPathResponse;
import com.org.example.entities.LearningPath;
import com.org.example.entities.Topic;
import com.org.example.repository.LearningPathRepository;
import com.org.example.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningPathService {

    private final LearningPathRepository learningPathRepository;
    private final TopicRepository topicRepository;

    /**
     * Add a topic to the learning path (called automatically when topic is created)
     */
    @Transactional
    public void addToLearningPath(Long userId, Long topicId) {
        // Get the next sequence number
        Integer maxSequence = learningPathRepository.findMaxSequenceNumber(userId);
        int nextSequence = (maxSequence == null) ? 1 : maxSequence + 1;

        // Get the previous topic (last in sequence)
        Long fromTopicId = null;
        if (nextSequence > 1) {
            List<LearningPath> paths = learningPathRepository.findByUserIdOrderBySequenceNumberAsc(userId);
            if (!paths.isEmpty()) {
                fromTopicId = paths.get(paths.size() - 1).getToTopicId();
            }
        }

        // Create learning path entry
        LearningPath learningPath = new LearningPath();
        learningPath.setUserId(userId);
        learningPath.setFromTopicId(fromTopicId);
        learningPath.setToTopicId(topicId);
        learningPath.setSequenceNumber(nextSequence);

        learningPathRepository.save(learningPath);
    }

    /**
     * Get the complete learning path for a user
     */
    public List<LearningPathResponse> getLearningPath(Long userId) {
        List<LearningPath> paths = learningPathRepository.findByUserIdOrderBySequenceNumberAsc(userId);

        return paths.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get the complete learning journey with topic details
     */
    public LearningJourneyResponse getLearningJourney(Long userId) {
        List<LearningPath> paths = learningPathRepository.findByUserIdOrderBySequenceNumberAsc(userId);

        if (paths.isEmpty()) {
            return LearningJourneyResponse.builder()
                    .totalTopics(0L)
                    .path(new ArrayList<>())
                    .build();
        }

        // Build journey entries
        List<LearningJourneyResponse.PathEntry> journeyEntries = new ArrayList<>();

        for (LearningPath path : paths) {
            Topic topic = topicRepository.findById(path.getToTopicId()).orElse(null);
            if (topic != null) {
                LearningJourneyResponse.PathEntry entry = LearningJourneyResponse.PathEntry.builder()
                        .sequenceNumber(path.getSequenceNumber())
                        .topicId(topic.getId())
                        .topicName(topic.getName())
                        .category(topic.getCategory())
                        .learnedDate(topic.getLearnedDate())
                        .confidenceLevel(topic.getConfidenceLevel())
                        .build();
                journeyEntries.add(entry);
            }
        }

        // Calculate journey stats
        LocalDate startDate = journeyEntries.stream()
                .map(LearningJourneyResponse.PathEntry::getLearnedDate)
                .filter(date -> date != null)
                .min(LocalDate::compareTo)
                .orElse(null);

        LocalDate endDate = journeyEntries.stream()
                .map(LearningJourneyResponse.PathEntry::getLearnedDate)
                .filter(date -> date != null)
                .max(LocalDate::compareTo)
                .orElse(null);

        Integer durationDays = null;
        if (startDate != null && endDate != null) {
            durationDays = (int) ChronoUnit.DAYS.between(startDate, endDate);
        }

        return LearningJourneyResponse.builder()
                .totalTopics((long) journeyEntries.size())
                .journeyStartDate(startDate)
                .durationDays(durationDays)
                .path(journeyEntries)
                .build();
    }

    /**
     * Remove a topic from learning path (when topic is deleted)
     */
    @Transactional
    public void removeFromLearningPath(Long userId, Long topicId) {
        learningPathRepository.deleteByUserIdAndToTopicId(userId, topicId);

        // Re-sequence remaining entries
        resequenceLearningPath(userId);
    }

    /**
     * Re-sequence the learning path after deletion
     */
    @Transactional
    private void resequenceLearningPath(Long userId) {
        List<LearningPath> paths = learningPathRepository.findByUserIdOrderBySequenceNumberAsc(userId);

        for (int i = 0; i < paths.size(); i++) {
            LearningPath path = paths.get(i);
            path.setSequenceNumber(i + 1);

            // Update fromTopicId
            if (i == 0) {
                path.setFromTopicId(null);
            } else {
                path.setFromTopicId(paths.get(i - 1).getToTopicId());
            }

            learningPathRepository.save(path);
        }
    }

    /**
     * Get learning path count
     */
    public Long getLearningPathCount(Long userId) {
        return learningPathRepository.countByUserId(userId);
    }

    /**
     * Helper: Convert entity to response DTO
     */
    private LearningPathResponse toResponse(LearningPath path) {
        String fromTopicName = null;
        if (path.getFromTopicId() != null) {
            fromTopicName = topicRepository.findById(path.getFromTopicId())
                    .map(Topic::getName)
                    .orElse(null);
        }

        String toTopicName = topicRepository.findById(path.getToTopicId())
                .map(Topic::getName)
                .orElse("Unknown Topic");

        return LearningPathResponse.builder()
                .id(path.getId())
                .sequenceNumber(path.getSequenceNumber())
                .fromTopicId(path.getFromTopicId())
                .fromTopicName(fromTopicName)
                .toTopicId(path.getToTopicId())
                .toTopicName(toTopicName)
                .createdAt(path.getCreatedAt())
                .build();
    }
}
