package com.org.example.service;

import com.org.example.dto.SkillEvolutionRequest;
import com.org.example.dto.SkillEvolutionResponse;
import com.org.example.dto.SkillFamilyRequest;
import com.org.example.dto.SkillFamilyResponse;
import com.org.example.entities.SkillEvolution;
import com.org.example.entities.SkillFamily;
import com.org.example.entities.Topic;
import com.org.example.exception.ResourceNotFoundException;
import com.org.example.repository.SkillEvolutionRepository;
import com.org.example.repository.SkillFamilyRepository;
import com.org.example.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillEvolutionService {

    private final SkillFamilyRepository skillFamilyRepository;
    private final SkillEvolutionRepository skillEvolutionRepository;
    private final TopicRepository topicRepository;

    // ==================== SKILL FAMILY OPERATIONS ====================

    /**
     * Create a new skill family
     */
    @Transactional
    public SkillFamilyResponse createSkillFamily(SkillFamilyRequest request) {
        SkillFamily family = new SkillFamily();
        family.setName(request.getName());
        family.setDescription(request.getDescription());
        family.setCategory(request.getCategory());

        SkillFamily saved = skillFamilyRepository.save(family);
        return toFamilyResponse(saved, false);
    }

    /**
     * Get skill family by ID
     */
    public SkillFamilyResponse getSkillFamilyById(Long id, boolean includeEvolutions) {
        SkillFamily family = skillFamilyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill family not found with id: " + id));

        return toFamilyResponse(family, includeEvolutions);
    }

    /**
     * Get all skill families
     */
    public List<SkillFamilyResponse> getAllSkillFamilies(boolean includeEvolutions) {
        List<SkillFamily> families = skillFamilyRepository.findAll();
        return families.stream()
                .map(family -> toFamilyResponse(family, includeEvolutions))
                .collect(Collectors.toList());
    }

    /**
     * Update skill family
     */
    @Transactional
    public SkillFamilyResponse updateSkillFamily(Long id, SkillFamilyRequest request) {
        SkillFamily family = skillFamilyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill family not found with id: " + id));

        if (request.getName() != null) {
            family.setName(request.getName());
        }
        if (request.getDescription() != null) {
            family.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            family.setCategory(request.getCategory());
        }

        SkillFamily updated = skillFamilyRepository.save(family);
        return toFamilyResponse(updated, true);
    }

    /**
     * Delete skill family
     */
    @Transactional
    public void deleteSkillFamily(Long id) {
        SkillFamily family = skillFamilyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill family not found with id: " + id));

        skillFamilyRepository.delete(family);
    }

    // ==================== SKILL EVOLUTION OPERATIONS ====================

    /**
     * Create skill evolution (link topic to family with level)
     */
    @Transactional
    public SkillEvolutionResponse createSkillEvolution(SkillEvolutionRequest request, Long userId) {
        // Validate skill family exists
        SkillFamily family = skillFamilyRepository.findById(request.getSkillFamilyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Skill family not found with id: " + request.getSkillFamilyId()));

        // Validate topic exists and belongs to user
        Topic topic = topicRepository.findByIdAndUserId(request.getTopicId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Topic not found or you don't have permission"));

        // Check if topic is already in a skill family
        if (skillEvolutionRepository.existsByTopicId(request.getTopicId())) {
            throw new IllegalArgumentException("Topic is already part of a skill evolution");
        }

        // Create skill evolution
        SkillEvolution evolution = new SkillEvolution();
        evolution.setSkillFamilyId(request.getSkillFamilyId());
        evolution.setTopicId(request.getTopicId());
        evolution.setLevel(request.getLevel());
        evolution.setComplexityScore(request.getComplexityScore());
        evolution.setAutomationLevel(request.getAutomationLevel());
        evolution.setPros(request.getPros());
        evolution.setCons(request.getCons());
        evolution.setUseCases(request.getUseCases());
        evolution.setCodeComparison(request.getCodeComparison());

        SkillEvolution saved = skillEvolutionRepository.save(evolution);
        return toEvolutionResponse(saved);
    }

    /**
     * Get all evolutions in a skill family
     */
    public List<SkillEvolutionResponse> getEvolutionsByFamily(Long familyId) {
        // Validate family exists
        skillFamilyRepository.findById(familyId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill family not found"));

        List<SkillEvolution> evolutions = skillEvolutionRepository
                .findBySkillFamilyIdOrderByLevelAsc(familyId);

        return evolutions.stream()
                .map(this::toEvolutionResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get evolution by ID
     */
    public SkillEvolutionResponse getEvolutionById(Long id) {
        SkillEvolution evolution = skillEvolutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill evolution not found"));

        return toEvolutionResponse(evolution);
    }

    /**
     * Update skill evolution
     */
    @Transactional
    public SkillEvolutionResponse updateSkillEvolution(Long id, SkillEvolutionRequest request) {
        SkillEvolution evolution = skillEvolutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill evolution not found"));

        if (request.getLevel() != null) {
            evolution.setLevel(request.getLevel());
        }
        if (request.getComplexityScore() != null) {
            evolution.setComplexityScore(request.getComplexityScore());
        }
        if (request.getAutomationLevel() != null) {
            evolution.setAutomationLevel(request.getAutomationLevel());
        }
        if (request.getPros() != null) {
            evolution.setPros(request.getPros());
        }
        if (request.getCons() != null) {
            evolution.setCons(request.getCons());
        }
        if (request.getUseCases() != null) {
            evolution.setUseCases(request.getUseCases());
        }
        if (request.getCodeComparison() != null) {
            evolution.setCodeComparison(request.getCodeComparison());
        }

        SkillEvolution updated = skillEvolutionRepository.save(evolution);
        return toEvolutionResponse(updated);
    }

    /**
     * Delete skill evolution
     */
    @Transactional
    public void deleteSkillEvolution(Long id) {
        SkillEvolution evolution = skillEvolutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill evolution not found"));

        skillEvolutionRepository.delete(evolution);
    }

    /**
     * Get evolution by topic ID
     */
    public SkillEvolutionResponse getEvolutionByTopic(Long topicId, Long userId) {
        // Validate topic ownership
        topicRepository.findByIdAndUserId(topicId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        SkillEvolution evolution = skillEvolutionRepository.findByTopicId(topicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "This topic is not part of any skill evolution"));

        return toEvolutionResponse(evolution);
    }

    // ==================== REGRESSION & ANALYTICS ====================

    /**
     * Check for regression risk (topics not reviewed recently)
     */
    public List<RegressionAlert> checkRegressionRisk(Long userId, Integer daysThreshold) {
        List<RegressionAlert> alerts = new ArrayList<>();

        if (daysThreshold == null) {
            daysThreshold = 90; // Default: 3 months
        }

        // Get all skill evolutions
        List<SkillEvolution> evolutions = skillEvolutionRepository.findAll();

        for (SkillEvolution evolution : evolutions) {
            Topic topic = topicRepository.findByIdAndUserId(evolution.getTopicId(), userId)
                    .orElse(null);

            if (topic != null && topic.getLastReviewed() != null) {
                long daysSinceReview = ChronoUnit.DAYS.between(topic.getLastReviewed(), LocalDate.now());

                if (daysSinceReview > daysThreshold) {
                    SkillFamily family = skillFamilyRepository.findById(evolution.getSkillFamilyId())
                            .orElse(null);

                    RegressionAlert alert = RegressionAlert.builder()
                            .familyId(evolution.getSkillFamilyId())
                            .familyName(family != null ? family.getName() : "Unknown")
                            .level(evolution.getLevel())
                            .topicId(topic.getId())
                            .topicName(topic.getName())
                            .lastReviewed(topic.getLastReviewed())
                            .daysSinceReview((int) daysSinceReview)
                            .riskLevel(calculateRiskLevel(daysSinceReview))
                            .build();

                    alerts.add(alert);
                }
            }
        }

        return alerts;
    }

    /**
     * Calculate risk level based on days since review
     */
    private String calculateRiskLevel(long daysSinceReview) {
        if (daysSinceReview > 180) {
            return "HIGH";
        } else if (daysSinceReview > 90) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Convert SkillFamily to Response DTO
     */
    private SkillFamilyResponse toFamilyResponse(SkillFamily family, boolean includeEvolutions) {
        Integer maxLevel = skillEvolutionRepository.findMaxLevelInFamily(family.getId());

        SkillFamilyResponse.SkillFamilyResponseBuilder builder = SkillFamilyResponse.builder()
                .id(family.getId())
                .name(family.getName())
                .description(family.getDescription())
                .category(family.getCategory())
                .totalLevels(maxLevel != null ? maxLevel : 0)
                .createdAt(family.getCreatedAt())
                .updatedAt(family.getUpdatedAt());

        if (includeEvolutions) {
            List<SkillEvolution> evolutions = skillEvolutionRepository
                    .findBySkillFamilyIdOrderByLevelAsc(family.getId());

            List<SkillFamilyResponse.SkillEvolutionSummary> summaries = evolutions.stream()
                    .map(e -> {
                        String topicName = topicRepository.findById(e.getTopicId())
                                .map(Topic::getName)
                                .orElse("Unknown");

                        return SkillFamilyResponse.SkillEvolutionSummary.builder()
                                .id(e.getId())
                                .level(e.getLevel())
                                .topicName(topicName)
                                .automationLevel(e.getAutomationLevel() != null ?
                                        e.getAutomationLevel().name() : null)
                                .complexityScore(e.getComplexityScore())
                                .build();
                    })
                    .collect(Collectors.toList());

            builder.evolutions(summaries);
        }

        return builder.build();
    }

    /**
     * Convert SkillEvolution to Response DTO
     */
    private SkillEvolutionResponse toEvolutionResponse(SkillEvolution evolution) {
        String familyName = skillFamilyRepository.findById(evolution.getSkillFamilyId())
                .map(SkillFamily::getName)
                .orElse("Unknown");

        String topicName = topicRepository.findById(evolution.getTopicId())
                .map(Topic::getName)
                .orElse("Unknown");

        return SkillEvolutionResponse.builder()
                .id(evolution.getId())
                .skillFamilyId(evolution.getSkillFamilyId())
                .skillFamilyName(familyName)
                .topicId(evolution.getTopicId())
                .topicName(topicName)
                .level(evolution.getLevel())
                .complexityScore(evolution.getComplexityScore())
                .automationLevel(evolution.getAutomationLevel())
                .pros(evolution.getPros())
                .cons(evolution.getCons())
                .useCases(evolution.getUseCases())
                .codeComparison(evolution.getCodeComparison())
                .createdAt(evolution.getCreatedAt())
                .updatedAt(evolution.getUpdatedAt())
                .build();
    }

    // ==================== REGRESSION ALERT DTO ====================

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class RegressionAlert {
        private Long familyId;
        private String familyName;
        private Integer level;
        private Long topicId;
        private String topicName;
        private LocalDate lastReviewed;
        private Integer daysSinceReview;
        private String riskLevel;  // LOW, MEDIUM, HIGH
    }
}
