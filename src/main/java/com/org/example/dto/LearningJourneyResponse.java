package com.org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningJourneyResponse {

    private Long totalTopics;
    private LocalDate journeyStartDate;
    private Integer durationDays;
    private List<PathEntry> path;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PathEntry {
        private Integer sequenceNumber;
        private Long topicId;
        private String topicName;
        private String category;
        private LocalDate learnedDate;
        private Integer confidenceLevel;
    }
}
