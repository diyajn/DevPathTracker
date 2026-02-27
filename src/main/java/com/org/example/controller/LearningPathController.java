package com.org.example.controller;


import com.org.example.dto.LearningJourneyResponse;
import com.org.example.dto.LearningPathResponse;
import com.org.example.entities.User;
import com.org.example.service.LearningPathService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/learning-path")
@RequiredArgsConstructor
@Tag(name = "Learning Path", description = "APIs for tracking learning journey and progression")
public class LearningPathController {

    private final LearningPathService learningPathService;

    // Get learning path (simple sequence)
    @GetMapping
    @Operation(summary = "Get learning path", description = "Retrieves the sequence of topics learned")
    public ResponseEntity<List<LearningPathResponse>> getLearningPath(Authentication authentication) {
        Long userId = extractUserId(authentication);
        List<LearningPathResponse> path = learningPathService.getLearningPath(userId);
        return ResponseEntity.ok(path);
    }

    // Get learning journey (with topic details and stats)
    @GetMapping("/journey")
    @Operation(summary = "Get learning journey", description = "Retrieves complete learning journey with statistics")
    public ResponseEntity<LearningJourneyResponse> getLearningJourney(Authentication authentication) {
        Long userId = extractUserId(authentication);
        LearningJourneyResponse journey = learningPathService.getLearningJourney(userId);
        return ResponseEntity.ok(journey);
    }

    // Get learning path count
    @GetMapping("/count")
    @Operation(summary = "Get path count", description = "Returns total number of topics in learning path")
    public ResponseEntity<Long> getLearningPathCount(Authentication authentication) {
        Long userId = extractUserId(authentication);
        Long count = learningPathService.getLearningPathCount(userId);
        return ResponseEntity.ok(count);
    }

    // Helper method
    private Long extractUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        System.out.println(user.getId());
        return user.getId();
    }
}

