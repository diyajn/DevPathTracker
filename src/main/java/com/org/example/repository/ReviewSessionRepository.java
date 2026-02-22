package com.org.example.repository;

import com.org.example.entities.ReviewSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewSessionRepository extends JpaRepository<ReviewSession, Long> {

    // Find all reviews for a topic
    List<ReviewSession> findByTopicIdOrderByReviewedAtDesc(Long topicId);

    // Find all reviews for a user
    List<ReviewSession> findByUserIdOrderByReviewedAtDesc(Long userId);

    // Find reviews for a topic by a specific user
    List<ReviewSession> findByTopicIdAndUserIdOrderByReviewedAtDesc(Long topicId, Long userId);

    // Find latest review for a topic
    Optional<ReviewSession> findFirstByTopicIdAndUserIdOrderByReviewedAtDesc(Long topicId, Long userId);

    // Find reviews within a date range
    List<ReviewSession> findByUserIdAndReviewedAtBetweenOrderByReviewedAtDesc(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // Find reviews due today or before
    @Query("SELECT r FROM ReviewSession r WHERE r.userId = :userId AND r.nextReviewDate <= :date")
    List<ReviewSession> findDueReviews(@Param("userId") Long userId, @Param("date") LocalDate date);

    // Count total reviews for a user
    Long countByUserId(Long userId);

    // Count reviews for a topic
    Long countByTopicIdAndUserId(Long topicId, Long userId);

    // Get average rating for a topic
    @Query("SELECT AVG(r.rating) FROM ReviewSession r WHERE r.topicId = :topicId AND r.userId = :userId")
    Double getAverageRatingForTopic(@Param("topicId") Long topicId, @Param("userId") Long userId);

    // Get total time spent reviewing
    @Query("SELECT SUM(r.timeSpentMinutes) FROM ReviewSession r WHERE r.userId = :userId")
    Long getTotalTimeSpent(@Param("userId") Long userId);

    // Count reviews in last N days
    @Query("SELECT COUNT(r) FROM ReviewSession r WHERE r.userId = :userId AND r.reviewedAt >= :sinceDate")
    Long countRecentReviews(@Param("userId") Long userId, @Param("sinceDate") LocalDateTime sinceDate);
}
