package com.org.example.dto;


import com.org.example.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private String githubUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
    private Integer durationDays;  // Calculated: endDate - startDate
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Linked topics
    private List<TopicSummary> topics;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopicSummary {
        private Long id;
        private String name;
        private String category;
    }
}
