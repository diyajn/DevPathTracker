package com.org.example.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private Long id;
    private Long topicId;
    private String topicName;
    private LocalDateTime reviewedAt;
    private Integer rating;
    private LocalDate nextReviewDate;
    private Integer timeSpentMinutes;
    private String notes;
    private LocalDateTime createdAt;
}