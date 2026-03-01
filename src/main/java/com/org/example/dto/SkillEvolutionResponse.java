package com.org.example.dto;


import com.org.example.enums.AutomationLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillEvolutionResponse {

    private Long id;
    private Long skillFamilyId;
    private String skillFamilyName;
    private Long topicId;
    private String topicName;
    private Integer level;
    private Integer complexityScore;
    private AutomationLevel automationLevel;
    private String pros;
    private String cons;
    private String useCases;
    private String codeComparison;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
