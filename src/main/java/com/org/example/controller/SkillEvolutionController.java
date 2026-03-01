package com.org.example.controller;

import com.org.example.dto.SkillEvolutionRequest;
import com.org.example.dto.SkillEvolutionResponse;
import com.org.example.dto.SkillFamilyRequest;
import com.org.example.dto.SkillFamilyResponse;
import com.org.example.entities.User;
import com.org.example.service.SkillEvolutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skill-evolution")
@RequiredArgsConstructor
@Tag(name = "Skill Evolution", description = "APIs for tracking skill progression from manual to automated")
public class SkillEvolutionController {

    private final SkillEvolutionService skillEvolutionService;

    // ==================== SKILL FAMILY ENDPOINTS ====================

    @PostMapping("/families")
    @Operation(summary = "Create skill family", description = "Creates a new skill family (e.g., Database Access)")
    public ResponseEntity<SkillFamilyResponse> createSkillFamily(
            @Valid @RequestBody SkillFamilyRequest request) {

        SkillFamilyResponse response = skillEvolutionService.createSkillFamily(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/families")
    @Operation(summary = "Get all skill families", description = "Retrieves all skill families")
    public ResponseEntity<List<SkillFamilyResponse>> getAllSkillFamilies(
            @RequestParam(defaultValue = "false") boolean includeEvolutions) {

        List<SkillFamilyResponse> families = skillEvolutionService
                .getAllSkillFamilies(includeEvolutions);
        return ResponseEntity.ok(families);
    }

    @GetMapping("/families/{id}")
    @Operation(summary = "Get skill family by ID", description = "Retrieves a specific skill family with evolutions")
    public ResponseEntity<SkillFamilyResponse> getSkillFamilyById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean includeEvolutions) {

        SkillFamilyResponse response = skillEvolutionService.getSkillFamilyById(id, includeEvolutions);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/families/{id}")
    @Operation(summary = "Update skill family", description = "Updates a skill family")
    public ResponseEntity<SkillFamilyResponse> updateSkillFamily(
            @PathVariable Long id,
            @Valid @RequestBody SkillFamilyRequest request) {

        SkillFamilyResponse response = skillEvolutionService.updateSkillFamily(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/families/{id}")
    @Operation(summary = "Delete skill family", description = "Deletes a skill family and all its evolutions")
    public ResponseEntity<Void> deleteSkillFamily(@PathVariable Long id) {
        skillEvolutionService.deleteSkillFamily(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== SKILL EVOLUTION ENDPOINTS ====================

    @PostMapping
    @Operation(summary = "Create skill evolution", description = "Links a topic to a skill family with a level")
    public ResponseEntity<SkillEvolutionResponse> createSkillEvolution(
            @Valid @RequestBody SkillEvolutionRequest request,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        SkillEvolutionResponse response = skillEvolutionService.createSkillEvolution(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/families/{familyId}/evolutions")
    @Operation(summary = "Get evolutions by family", description = "Retrieves all evolution levels in a family")
    public ResponseEntity<List<SkillEvolutionResponse>> getEvolutionsByFamily(
            @PathVariable Long familyId) {

        List<SkillEvolutionResponse> evolutions = skillEvolutionService.getEvolutionsByFamily(familyId);
        return ResponseEntity.ok(evolutions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get evolution by ID", description = "Retrieves a specific skill evolution")
    public ResponseEntity<SkillEvolutionResponse> getEvolutionById(@PathVariable Long id) {
        SkillEvolutionResponse response = skillEvolutionService.getEvolutionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/topics/{topicId}")
    @Operation(summary = "Get evolution by topic", description = "Retrieves skill evolution for a specific topic")
    public ResponseEntity<SkillEvolutionResponse> getEvolutionByTopic(
            @PathVariable Long topicId,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        SkillEvolutionResponse response = skillEvolutionService.getEvolutionByTopic(topicId, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update skill evolution", description = "Updates skill evolution details")
    public ResponseEntity<SkillEvolutionResponse> updateSkillEvolution(
            @PathVariable Long id,
            @Valid @RequestBody SkillEvolutionRequest request) {

        SkillEvolutionResponse response = skillEvolutionService.updateSkillEvolution(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete skill evolution", description = "Removes a topic from skill evolution")
    public ResponseEntity<Void> deleteSkillEvolution(@PathVariable Long id) {
        skillEvolutionService.deleteSkillEvolution(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== REGRESSION & ANALYTICS ====================

    @GetMapping("/regression-check")
    @Operation(summary = "Check regression risk", description = "Identifies skills at risk of being forgotten")
    public ResponseEntity<List<SkillEvolutionService.RegressionAlert>> checkRegressionRisk(
            @RequestParam(required = false) Integer daysThreshold,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        List<SkillEvolutionService.RegressionAlert> alerts =
                skillEvolutionService.checkRegressionRisk(userId, daysThreshold);
        return ResponseEntity.ok(alerts);
    }

    // Helper method
    private Long extractUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        System.out.println(user.getId());
        return user.getId();
    }
}