package com.org.example.mapper;

import com.org.example.dto.ReviewRequest;
import com.org.example.dto.ReviewResponse;
import com.org.example.entities.ReviewSession;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReviewMapper {

    // Convert Entity to Response DTO
    public ReviewResponse toResponse(ReviewSession review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .topicId(review.getTopicId())
                .reviewedAt(review.getReviewedAt())
                .rating(review.getRating())
                .nextReviewDate(review.getNextReviewDate())
                .timeSpentMinutes(review.getTimeSpentMinutes())
                .notes(review.getNotes())
                .createdAt(review.getCreatedAt())
                .build();
    }

    // Convert Entity to Response DTO with topic name
    public ReviewResponse toResponse(ReviewSession review, String topicName) {
        ReviewResponse response = toResponse(review);
        response.setTopicName(topicName);
        return response;
    }

    // Convert Request DTO to Entity
    public ReviewSession toEntity(ReviewRequest request, Long topicId, Long userId) {
        ReviewSession review = new ReviewSession();
        review.setTopicId(topicId);
        review.setUserId(userId);
        review.setReviewedAt(LocalDateTime.now());
        review.setRating(request.getRating());
        review.setTimeSpentMinutes(request.getTimeSpentMinutes());
        review.setNotes(request.getNotes());
        return review;
    }
}
