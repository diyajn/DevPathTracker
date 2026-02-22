package com.org.example.controller;

import com.org.example.dto.ReviewRequest;
import com.org.example.dto.ReviewResponse;
import com.org.example.dto.ReviewStatsResponse;
import com.org.example.entities.User;
import com.org.example.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Review Management", description = "APIs for tracking review sessions and spaced repetition")
public class ReviewController {

    private final ReviewService reviewService;

    // Record a review for a topic
    @PostMapping("/topics/{topicId}")
    @Operation(summary = "Record a review", description = "Records a review session for a topic with rating (1-5)")
    public ResponseEntity<ReviewResponse> recordReview(
            @PathVariable Long topicId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        ReviewResponse response = reviewService.recordReview(topicId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all reviews for a topic
    @GetMapping("/topics/{topicId}")
    @Operation(summary = "Get reviews for a topic", description = "Retrieves all review sessions for a specific topic")
    public ResponseEntity<List<ReviewResponse>> getReviewsForTopic(
            @PathVariable Long topicId,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        List<ReviewResponse> reviews = reviewService.getReviewsForTopic(topicId, userId);
        return ResponseEntity.ok(reviews);
    }

    // Get latest review for a topic
    @GetMapping("/topics/{topicId}/latest")
    @Operation(summary = "Get latest review", description = "Retrieves the most recent review for a topic")
    public ResponseEntity<ReviewResponse> getLatestReview(
            @PathVariable Long topicId,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        ReviewResponse review = reviewService.getLatestReview(topicId, userId);
        return ResponseEntity.ok(review);
    }

    // Get all reviews for user
    @GetMapping
    @Operation(summary = "Get all reviews", description = "Retrieves all review sessions for the authenticated user")
    public ResponseEntity<List<ReviewResponse>> getAllReviews(Authentication authentication) {
        Long userId = extractUserId(authentication);
        List<ReviewResponse> reviews = reviewService.getAllReviews(userId);
        return ResponseEntity.ok(reviews);
    }

    // Get reviews within date range
    @GetMapping("/range")
    @Operation(summary = "Get reviews by date range", description = "Retrieves reviews within a specific date range")
    public ResponseEntity<List<ReviewResponse>> getReviewsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        List<ReviewResponse> reviews = reviewService.getReviewsByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(reviews);
    }

    // Get review statistics
    @GetMapping("/stats")
    @Operation(summary = "Get review statistics", description = "Returns statistics about review sessions")
    public ResponseEntity<ReviewStatsResponse> getReviewStats(Authentication authentication) {
        Long userId = extractUserId(authentication);
        ReviewStatsResponse stats = reviewService.getReviewStats(userId);
        return ResponseEntity.ok(stats);
    }

    // Get topics due for review
    @GetMapping("/due")
    @Operation(summary = "Get topics due for review", description = "Returns list of topic IDs that need review today")
    public ResponseEntity<List<Long>> getTopicsDueForReview(Authentication authentication) {
        Long userId = extractUserId(authentication);
        List<Long> topicIds = reviewService.getTopicsDueForReview(userId);
        return ResponseEntity.ok(topicIds);
    }

    private Long extractUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        System.out.println(user.getId());
        return user.getId();
    }
}