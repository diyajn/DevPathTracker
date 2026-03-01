package com.org.example.dto;


import com.org.example.enums.AutomationLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillEvolutionRequest {

    @NotNull(message = "Skill family ID is required")
    private Long skillFamilyId;

    @NotNull(message = "Topic ID is required")
    private Long topicId;

    @NotNull(message = "Level is required")
    @Min(value = 1, message = "Level must be at least 1")
    private Integer level;

    @Min(value = 1, message = "Complexity score must be between 1 and 10")
    @Max(value = 10, message = "Complexity score must be between 1 and 10")
    private Integer complexityScore;

    private AutomationLevel automationLevel;

    private String pros;

    private String cons;

    private String useCases;

    private String codeComparison;
}
