package com.org.example.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillFamilyResponse {

    private Long id;
    private String name;
    private String description;
    private String category;
    private Integer totalLevels;  // How many evolution levels exist
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // List of evolutions in this family (optional, for detailed view)
    private List<SkillEvolutionSummary> evolutions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SkillEvolutionSummary {
        private Long id;
        private Integer level;
        private String topicName;
        private String automationLevel;
        private Integer complexityScore;
    }
}
