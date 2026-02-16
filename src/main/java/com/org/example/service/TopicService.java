package com.org.example.service;

import com.org.example.dto.TopicRequest;
import com.org.example.dto.TopicResponse;
import com.org.example.entities.Topic;
import com.org.example.exception.ResourceNotFoundException;
import com.org.example.exception.UnauthorizedException;
import com.org.example.mapper.TopicMapper;
import com.org.example.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;

    // Create a new topic
    @Transactional
    public TopicResponse createTopic(TopicRequest request, Long userId) {
        // Validate parent topic exists and belongs to user (if provided)
        if (request.getParentTopicId() != null) {
            validateTopicOwnership(request.getParentTopicId(), userId);
        }

        Topic topic = topicMapper.toEntity(request, userId);

        // Set learned date to today if not provided
        if (topic.getLearnedDate() == null) {
            topic.setLearnedDate(LocalDate.now());
        }

        // Set last reviewed to learned date initially
        topic.setLastReviewed(topic.getLearnedDate());

        Topic savedTopic = topicRepository.save(topic);

        return enrichTopicResponse(savedTopic);
    }

    // Get topic by ID
    public TopicResponse getTopicById(Long id, Long userId) {
        Topic topic = topicRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));

        return enrichTopicResponse(topic);
    }

    // Get all topics for a user
    public List<TopicResponse> getAllTopics(Long userId) {
        List<Topic> topics = topicRepository.findByUserId(userId);
        return topics.stream()
                .map(this::enrichTopicResponse)
                .collect(Collectors.toList());
    }

    // Get topics by category
    public List<TopicResponse> getTopicsByCategory(String category, Long userId) {
        List<Topic> topics = topicRepository.findByUserIdAndCategory(userId, category);
        return topics.stream()
                .map(this::enrichTopicResponse)
                .collect(Collectors.toList());
    }

    // Get root topics (topics with no parent)
    public List<TopicResponse> getRootTopics(Long userId) {
        List<Topic> topics = topicRepository.findByUserIdAndParentTopicIdIsNull(userId);
        return topics.stream()
                .map(this::enrichTopicResponse)
                .collect(Collectors.toList());
    }

    // Get child topics of a parent
    public List<TopicResponse> getChildTopics(Long parentId, Long userId) {
        // Verify parent topic exists and belongs to user
        validateTopicOwnership(parentId, userId);

        List<Topic> topics = topicRepository.findByUserIdAndParentTopicId(userId, parentId);
        return topics.stream()
                .map(this::enrichTopicResponse)
                .collect(Collectors.toList());
    }

    // Update topic
    @Transactional
    public TopicResponse updateTopic(Long id, TopicRequest request, Long userId) {
        Topic topic = topicRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));

        // Validate new parent topic if being changed
        if (request.getParentTopicId() != null &&
                !request.getParentTopicId().equals(topic.getParentTopicId())) {

            // Prevent setting self as parent
            if (request.getParentTopicId().equals(id)) {
                throw new IllegalArgumentException("Topic cannot be its own parent");
            }

            // Prevent circular reference
            if (isDescendant(id, request.getParentTopicId(), userId)) {
                throw new IllegalArgumentException("Cannot set a descendant as parent (circular reference)");
            }

            validateTopicOwnership(request.getParentTopicId(), userId);
        }

        topicMapper.updateEntity(topic, request);
        Topic updatedTopic = topicRepository.save(topic);

        return enrichTopicResponse(updatedTopic);
    }

    // Delete topic
    @Transactional
    public void deleteTopic(Long id, Long userId) {
        Topic topic = topicRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));

        topicRepository.delete(topic);
    }

    // Get topics with low confidence (at-risk topics)
    public List<TopicResponse> getAtRiskTopics(Long userId, Integer maxConfidence) {
        List<Topic> topics = topicRepository.findByUserIdAndConfidenceLevelLessThan(userId, maxConfidence);
        return topics.stream()
                .map(this::enrichTopicResponse)
                .collect(Collectors.toList());
    }

    // Get total topic count for user
    public Long getTopicCount(Long userId) {
        return topicRepository.countByUserId(userId);
    }

    // Helper: Validate topic ownership
    private void validateTopicOwnership(Long topicId, Long userId) {
        if (!topicRepository.existsByIdAndUserId(topicId, userId)) {
            throw new UnauthorizedException("You don't have permission to access this topic");
        }
    }

    // Helper: Check if a topic is a descendant of another (prevent circular reference)
    private boolean isDescendant(Long ancestorId, Long descendantId, Long userId) {
        Topic current = topicRepository.findByIdAndUserId(descendantId, userId).orElse(null);

        while (current != null && current.getParentTopicId() != null) {
            if (current.getParentTopicId().equals(ancestorId)) {
                return true;
            }
            current = topicRepository.findByIdAndUserId(current.getParentTopicId(), userId).orElse(null);
        }

        return false;
    }

    // Helper: Enrich topic response with parent name and children info
    private TopicResponse enrichTopicResponse(Topic topic) {
        String parentName = null;
        if (topic.getParentTopicId() != null) {
            parentName = topicRepository.findById(topic.getParentTopicId())
                    .map(Topic::getName)
                    .orElse(null);
        }

        boolean hasChildren = !topicRepository.findByUserIdAndParentTopicId(
                topic.getUserId(), topic.getId()).isEmpty();

        return topicMapper.toResponse(topic, parentName, hasChildren);
    }
}