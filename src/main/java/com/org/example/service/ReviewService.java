package com.org.example.service;


import com.org.example.dto.ReviewRequest;
import com.org.example.dto.ReviewResponse;
import com.org.example.dto.ReviewStatsResponse;
import com.org.example.entities.ReviewSession;
import com.org.example.entities.Topic;
import com.org.example.exception.ResourceNotFoundException;
import com.org.example.exception.UnauthorizedException;
import com.org.example.mapper.ReviewMapper;
import com.org.example.repository.ReviewSessionRepository;
import com.org.example.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewSessionRepository reviewRepository;
    private final TopicRepository topicRepository;
    private final ReviewMapper reviewMapper;

    // Record a review session
    @Transactional
    public ReviewResponse recordReview(Long topicId, ReviewRequest request, Long userId) {
        // Verify topic ownership
        Topic topic = validateTopicOwnership(topicId, userId);

        // Create review session
        ReviewSession review = reviewMapper.toEntity(request, topicId, userId);

        // Calculate simple next review date (will be improved in Step 6 with SM-2)
        LocalDate nextReviewDate = calculateSimpleNextReviewDate(request.getRating());
        review.setNextReviewDate(nextReviewDate);

        // Save review
        ReviewSession savedReview = reviewRepository.save(review);

        // Update topic's last reviewed date and confidence
        topic.setLastReviewed(LocalDate.now());
        updateTopicConfidence(topic, userId);
        topicRepository.save(topic);

        return reviewMapper.toResponse(savedReview, topic.getName());
    }

    // Get all reviews for a topic
    public List<ReviewResponse> getReviewsForTopic(Long topicId, Long userId) {
        // Verify topic ownership
        Topic topic = validateTopicOwnership(topicId, userId);

        List<ReviewSession> reviews = reviewRepository.findByTopicIdAndUserIdOrderByReviewedAtDesc(
                topicId, userId);

        return reviews.stream()
                .map(review -> reviewMapper.toResponse(review, topic.getName()))
                .collect(Collectors.toList());
    }

    // Get all reviews for a user
    public List<ReviewResponse> getAllReviews(Long userId) {
        List<ReviewSession> reviews = reviewRepository.findByUserIdOrderByReviewedAtDesc(userId);

        return reviews.stream()
                .map(review -> {
                    String topicName = topicRepository.findById(review.getTopicId())
                            .map(Topic::getName)
                            .orElse("Unknown Topic");
                    return reviewMapper.toResponse(review, topicName);
                })
                .collect(Collectors.toList());
    }

    // Get latest review for a topic
    public ReviewResponse getLatestReview(Long topicId, Long userId) {
        // Verify topic ownership
        Topic topic = validateTopicOwnership(topicId, userId);

        ReviewSession review = reviewRepository.findFirstByTopicIdAndUserIdOrderByReviewedAtDesc(
                        topicId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No review found for topic: " + topicId));

        return reviewMapper.toResponse(review, topic.getName());
    }

    // Get reviews within date range
    public List<ReviewResponse> getReviewsByDateRange(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        List<ReviewSession> reviews = reviewRepository
                .findByUserIdAndReviewedAtBetweenOrderByReviewedAtDesc(userId, startDate, endDate);

        return reviews.stream()
                .map(review -> {
                    String topicName = topicRepository.findById(review.getTopicId())
                            .map(Topic::getName)
                            .orElse("Unknown Topic");
                    return reviewMapper.toResponse(review, topicName);
                })
                .collect(Collectors.toList());
    }

    // Get review statistics
    public ReviewStatsResponse getReviewStats(Long userId) {
        Long totalReviews = reviewRepository.countByUserId(userId);

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        Long reviewsLast7Days = reviewRepository.countRecentReviews(userId, sevenDaysAgo);
        Long reviewsLast30Days = reviewRepository.countRecentReviews(userId, thirtyDaysAgo);

        Long totalTimeSpent = reviewRepository.getTotalTimeSpent(userId);
        if (totalTimeSpent == null) totalTimeSpent = 0L;

        Double averageTime = totalReviews > 0 ?
                (double) totalTimeSpent / totalReviews : 0.0;

        // Count unique topics reviewed - FIX HERE
        Long topicsReviewed = reviewRepository.findByUserIdOrderByReviewedAtDesc(userId)
            .stream()
            .map(ReviewSession::getTopicId)
            .distinct()
            .count();

        return ReviewStatsResponse.builder()
                .totalReviews(totalReviews)
                .reviewsLast7Days(reviewsLast7Days)
                .reviewsLast30Days(reviewsLast30Days)
                .totalTimeSpentMinutes(totalTimeSpent)
                .averageTimePerReview(averageTime)
                .topicsReviewedCount(topicsReviewed)
                .build();
    }

    // Get topics due for review
    public List<Long> getTopicsDueForReview(Long userId) {
        List<ReviewSession> dueReviews = reviewRepository.findDueReviews(userId, LocalDate.now());

        return dueReviews.stream()
                .map(ReviewSession::getTopicId)
                .distinct()
                .collect(Collectors.toList());
    }

    // Helper: Simple next review date calculation (improved in Step 6)
    private LocalDate calculateSimpleNextReviewDate(Integer rating) {
        LocalDate today = LocalDate.now();

        switch (rating) {
            case 1: return today.plusDays(1);   // Hard: review tomorrow
            case 2: return today.plusDays(2);   // Okay: review in 2 days
            case 3: return today.plusDays(4);   // Good: review in 4 days
            case 4: return today.plusDays(7);   // Easy: review in 1 week
            case 5: return today.plusDays(14);  // Perfect: review in 2 weeks
            default: return today.plusDays(1);
        }
    }

    // Helper: Update topic confidence based on recent reviews
    private void updateTopicConfidence(Topic topic, Long userId) {
        Double avgRating = reviewRepository.getAverageRatingForTopic(topic.getId(), userId);

        if (avgRating != null) {
            // Round to nearest integer (1-5)
            int newConfidence = (int) Math.round(avgRating);
            topic.setConfidenceLevel(newConfidence);
        }
    }

    // Helper: Validate topic ownership
    private Topic validateTopicOwnership(Long topicId, Long userId) {
        return topicRepository.findByIdAndUserId(topicId, userId)
                .orElseThrow(() -> new UnauthorizedException(
                        "Topic not found or you don't have permission"));
    }
}