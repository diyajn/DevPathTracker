package com.org.example.dto;

import com.org.example.enums.DifficultyType;
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
public class TopicResponse {

    private Long id;
    private String name;
    private String category;
    private DifficultyType difficulty;
    private LocalDate learnedDate;
    private LocalDate lastReviewed;
    private Integer confidenceLevel;
    private Long parentTopicId;
    private String parentTopicName;  // Name of parent topic (if exists)
    private String tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean hasChildren;  // Does this topic have child topics?
}
