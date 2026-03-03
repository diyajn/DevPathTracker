package com.org.example.service;


import com.org.example.dto.CategoryStats;
import com.org.example.dto.DashboardStatsResponse;
import com.org.example.dto.MonthlyProgress;
import com.org.example.dto.StreakInfo;
import com.org.example.entities.ReviewSession;
import com.org.example.entities.Topic;
import com.org.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TopicRepository topicRepository;
    private final ReviewSessionRepository reviewRepository;
    private final ProjectRepository projectRepository;
    private final NoteRepository noteRepository;
    private final LearningPathRepository learningPathRepository;
    private final SkillFamilyRepository skillFamilyRepository;
    private final SkillEvolutionRepository skillEvolutionRepository;

    public DashboardStatsResponse getDashboardStats(Long userId) {
        return DashboardStatsResponse.builder()
                .totalTopics(getTotalTopics(userId))
                .totalProjects(getTotalProjects(userId))
                .totalReviews(getTotalReviews(userId))
                .totalNotes(getTotalNotes(userId))
                .reviewsThisWeek(getReviewsThisWeek(userId))
                .reviewsThisMonth(getReviewsThisMonth(userId))
                .averageReviewRating(getAverageReviewRating(userId))
                .totalTimeSpentMinutes(getTotalTimeSpent(userId))
                .averageConfidence(getAverageConfidence(userId))
                .weakTopics(getWeakTopicsCount(userId))
                .moderateTopics(getModerateTopicsCount(userId))
                .strongTopics(getStrongTopicsCount(userId))
                .currentStreak(calculateCurrentStreak(userId))
                .topicsDueToday(getTopicsDueToday(userId))
                .topicsOverdue(getTopicsOverdue(userId))
                .atRiskTopics(getAtRiskTopicsCount(userId))
                .topicsByCategory(getTopicsByCategory(userId))
                .monthlyProgress(getMonthlyProgress(userId))
                .totalSkillFamilies((int) skillFamilyRepository.findAll().size())
                .totalEvolutionLevels((int) skillEvolutionRepository.findAll().size())
                .learningPathLength(getLearningPathLength(userId))
                .journeyStartDate(getJourneyStartDate(userId))
                .journeyDurationDays(getJourneyDuration(userId))
                .build();
    }

    public StreakInfo getStreakInfo(Long userId) {
        int currentStreak = calculateCurrentStreak(userId);
        int longestStreak = calculateLongestStreak(userId);
        LocalDate lastReview = getLastReviewDate(userId);
        boolean reviewedToday = isReviewedToday(userId);

        return StreakInfo.builder()
                .currentStreak(currentStreak)
                .longestStreak(longestStreak)
                .lastReviewDate(lastReview)
                .reviewedToday(reviewedToday)
                .build();
    }

    // ==================== BASIC COUNTS ====================

    private Long getTotalTopics(Long userId) {
        return topicRepository.countByUserId(userId);
    }

    private Long getTotalProjects(Long userId) {
        return projectRepository.countByUserId(userId);
    }

    private Long getTotalReviews(Long userId) {
        return reviewRepository.countByUserId(userId);
    }

    private Long getTotalNotes(Long userId) {
        List<Topic> topics = topicRepository.findByUserId(userId);
        return topics.stream()
                .mapToLong(topic -> noteRepository.countByTopicId(topic.getId()))
                .sum();
    }

    // ==================== REVIEW STATS ====================

    private Long getReviewsThisWeek(Long userId) {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        return reviewRepository.countRecentReviews(userId, weekAgo);
    }

    private Long getReviewsThisMonth(Long userId) {
        LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
        return reviewRepository.countRecentReviews(userId, monthAgo);
    }

    private Double getAverageReviewRating(Long userId) {
        List<ReviewSession> reviews = reviewRepository.findByUserIdOrderByReviewedAtDesc(userId);
        if (reviews.isEmpty()) return 0.0;

        double avgRating = reviews.stream()
                .mapToInt(ReviewSession::getRating)
                .average()
                .orElse(0.0);

        return Math.round(avgRating * 100.0) / 100.0;
    }

    private Long getTotalTimeSpent(Long userId) {
        Long total = reviewRepository.getTotalTimeSpent(userId);
        return total != null ? total : 0L;
    }

    // ==================== CONFIDENCE STATS ====================

    private Double getAverageConfidence(Long userId) {
        List<Topic> topics = topicRepository.findByUserId(userId);
        if (topics.isEmpty()) return 0.0;

        double avgConfidence = topics.stream()
                .filter(t -> t.getConfidenceLevel() != null)
                .mapToInt(Topic::getConfidenceLevel)
                .average()
                .orElse(0.0);

        return Math.round(avgConfidence * 100.0) / 100.0;
    }

    private Long getWeakTopicsCount(Long userId) {
        return (long) topicRepository.findByUserIdAndConfidenceLevelLessThan(userId, 3).size();
    }

    private Long getModerateTopicsCount(Long userId) {
        List<Topic> topics = topicRepository.findByUserId(userId);
        return topics.stream()
                .filter(t -> t.getConfidenceLevel() != null && t.getConfidenceLevel() == 3)
                .count();
    }

    private Long getStrongTopicsCount(Long userId) {
        List<Topic> topics = topicRepository.findByUserId(userId);
        return topics.stream()
                .filter(t -> t.getConfidenceLevel() != null && t.getConfidenceLevel() >= 4)
                .count();
    }

    // ==================== DUE TOPICS ====================

    private Long getTopicsDueToday(Long userId) {
        List<ReviewSession> dueReviews = reviewRepository.findDueReviews(userId, LocalDate.now());
        return (long) dueReviews.size();
    }

    private Long getTopicsOverdue(Long userId) {
        List<ReviewSession> overdueReviews = reviewRepository.findDueReviews(
                userId,
                LocalDate.now().minusDays(1)
        );
        return (long) overdueReviews.size();
    }

    private Long getAtRiskTopicsCount(Long userId) {
        LocalDate threshold = LocalDate.now().minusDays(90);
        List<Topic> topics = topicRepository.findByUserId(userId);

        return topics.stream()
                .filter(t -> t.getLastReviewed() != null && t.getLastReviewed().isBefore(threshold))
                .count();
    }

    // ==================== CATEGORY BREAKDOWN ====================

    private List<CategoryStats> getTopicsByCategory(Long userId) {
        List<Topic> topics = topicRepository.findByUserId(userId);

        Map<String, List<Topic>> byCategory = topics.stream()
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(Topic::getCategory));

        return byCategory.entrySet().stream()
                .map(entry -> {
                    String category = entry.getKey();
                    List<Topic> categoryTopics = entry.getValue();

                    double avgConfidence = categoryTopics.stream()
                            .filter(t -> t.getConfidenceLevel() != null)
                            .mapToInt(Topic::getConfidenceLevel)
                            .average()
                            .orElse(0.0);

                    return CategoryStats.builder()
                            .category(category)
                            .count((long) categoryTopics.size())
                            .averageConfidence(Math.round(avgConfidence * 100.0) / 100.0)
                            .build();
                })
                .sorted(Comparator.comparing(CategoryStats::getCount).reversed())
                .collect(Collectors.toList());
    }

    // ==================== MONTHLY PROGRESS ====================

    private List<MonthlyProgress> getMonthlyProgress(Long userId) {
        List<Topic> topics = topicRepository.findByUserId(userId);
        List<ReviewSession> reviews = reviewRepository.findByUserIdOrderByReviewedAtDesc(userId);

        Map<String, Long> topicsByMonth = topics.stream()
                .filter(t -> t.getLearnedDate() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getLearnedDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()
                ));

        Map<String, Long> reviewsByMonth = reviews.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getReviewedAt().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()
                ));

        List<MonthlyProgress> progress = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 11; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            String monthKey = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String monthName = month.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

            progress.add(MonthlyProgress.builder()
                    .month(monthKey)
                    .monthName(monthName)
                    .topicsLearned(topicsByMonth.getOrDefault(monthKey, 0L))
                    .reviewsCompleted(reviewsByMonth.getOrDefault(monthKey, 0L))
                    .build());
        }

        return progress;
    }

    // ==================== STREAK CALCULATION ====================

    private Integer calculateCurrentStreak(Long userId) {
        List<ReviewSession> reviews = reviewRepository.findByUserIdOrderByReviewedAtDesc(userId);
        if (reviews.isEmpty()) return 0;

        Set<LocalDate> reviewDates = reviews.stream()
                .map(r -> r.getReviewedAt().toLocalDate())
                .collect(Collectors.toSet());

        LocalDate today = LocalDate.now();
        int streak = 0;

        if (!reviewDates.contains(today) && !reviewDates.contains(today.minusDays(1))) {
            return 0;
        }

        LocalDate checkDate = today;
        while (reviewDates.contains(checkDate)) {
            streak++;
            checkDate = checkDate.minusDays(1);
        }

        return streak;
    }

    private Integer calculateLongestStreak(Long userId) {
        List<ReviewSession> reviews = reviewRepository.findByUserIdOrderByReviewedAtDesc(userId);
        if (reviews.isEmpty()) return 0;

        Set<LocalDate> reviewDates = reviews.stream()
                .map(r -> r.getReviewedAt().toLocalDate())
                .collect(Collectors.toSet());

        List<LocalDate> sortedDates = new ArrayList<>(reviewDates);
        Collections.sort(sortedDates);

        int longestStreak = 0;
        int currentStreak = 1;

        for (int i = 1; i < sortedDates.size(); i++) {
            long daysBetween = ChronoUnit.DAYS.between(sortedDates.get(i - 1), sortedDates.get(i));

            if (daysBetween == 1) {
                currentStreak++;
            } else {
                longestStreak = Math.max(longestStreak, currentStreak);
                currentStreak = 1;
            }
        }

        return Math.max(longestStreak, currentStreak);
    }

    private LocalDate getLastReviewDate(Long userId) {
        return reviewRepository.findFirstByUserIdOrderByReviewedAtDesc(userId)
                .map(r -> r.getReviewedAt().toLocalDate())
                .orElse(null);
    }

    private Boolean isReviewedToday(Long userId) {
        LocalDate today = LocalDate.now();
        List<ReviewSession> reviews = reviewRepository.findByUserIdOrderByReviewedAtDesc(userId);

        return reviews.stream()
                .anyMatch(r -> r.getReviewedAt().toLocalDate().equals(today));
    }

    // ==================== LEARNING PATH ====================

    private Integer getLearningPathLength(Long userId) {
        Long count = learningPathRepository.countByUserId(userId);
        return count != null ? count.intValue() : 0;
    }

    private String getJourneyStartDate(Long userId) {
        return topicRepository.findByUserId(userId).stream()
                .map(Topic::getLearnedDate)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .map(LocalDate::toString)
                .orElse(null);
    }

    private Integer getJourneyDuration(Long userId) {
        List<Topic> topics = topicRepository.findByUserId(userId);

        Optional<LocalDate> start = topics.stream()
                .map(Topic::getLearnedDate)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo);

        Optional<LocalDate> end = topics.stream()
                .map(Topic::getLearnedDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo);

        if (start.isPresent() && end.isPresent()) {
            return (int) ChronoUnit.DAYS.between(start.get(), end.get());
        }

        return 0;
    }
}