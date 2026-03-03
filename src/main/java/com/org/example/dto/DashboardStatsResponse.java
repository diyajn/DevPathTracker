package com.org.example.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {

    // Overall Stats
    private Long totalTopics;
    private Long totalProjects;
    private Long totalReviews;
    private Long totalNotes;

    // Review Stats
    private Long reviewsThisWeek;
    private Long reviewsThisMonth;
    private Double averageReviewRating;
    private Long totalTimeSpentMinutes;

    // Confidence Stats
    private Double averageConfidence;
    private Long weakTopics;        // confidence <= 2
    private Long moderateTopics;    // confidence = 3
    private Long strongTopics;      // confidence >= 4

    // Current Status
    private Integer currentStreak;     // Days in a row with reviews
    private Long topicsDueToday;
    private Long topicsOverdue;
    private Long atRiskTopics;         // Not reviewed in 90+ days

    // Category Breakdown
    private List<CategoryStats> topicsByCategory;

    // Monthly Progress
    private List<MonthlyProgress> monthlyProgress;  // Last 12 months

    // Skill Evolution Summary
    private Integer totalSkillFamilies;
    private Integer totalEvolutionLevels;

    // Learning Path
    private Integer learningPathLength;
    private String journeyStartDate;
    private Integer journeyDurationDays;
}