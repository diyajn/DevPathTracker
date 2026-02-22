package com.org.example.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewStatsResponse {

    private Long totalReviews;
    private Long reviewsLast7Days;
    private Long reviewsLast30Days;
    private Long totalTimeSpentMinutes;
    private Double averageTimePerReview;
    private Long topicsReviewedCount;
}
