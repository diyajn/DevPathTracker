package com.org.example.dto;

import com.org.example.enums.DifficultyType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicRequest {
    @NotBlank(message = "Topic name is required")
    @Size(min = 3, max = 200, message = "Topic name must be between 3 and 200 characters")
    private String name;

    private String category;

    private DifficultyType difficulty;

    private LocalDate learnedDate;

    private Long parentTopicId;

    @Size(max = 500, message = "Tags cannot exceed 500 characters")
    private String tags;

    @Min(value = 1, message = "Confidence level must be between 1 and 5")
    @Max(value = 5, message = "Confidence level must be between 1 and 5")
    private Integer confidenceLevel;
}
