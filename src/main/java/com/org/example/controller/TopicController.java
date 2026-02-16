package com.org.example.controller;

import com.org.example.dto.TopicRequest;
import com.org.example.dto.TopicResponse;
import com.org.example.entities.User;
import com.org.example.service.TopicService;
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
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@Tag(name = "Topic Management", description = "APIs for managing learning topics")
public class TopicController {

    private final TopicService topicService;

    // Create a new topic
    @PostMapping
    @Operation(summary = "Create a new topic", description = "Creates a new learning topic for the authenticated user")
    public ResponseEntity<TopicResponse> createTopic(
            @Valid @RequestBody TopicRequest request,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        TopicResponse response = topicService.createTopic(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all topics (with optional category filter)
    @GetMapping
    @Operation(summary = "Get all topics", description = "Retrieves all topics for the authenticated user, optionally filtered by category")
    public ResponseEntity<List<TopicResponse>> getAllTopics(
            @RequestParam(required = false) String category,
            Authentication authentication) {

        Long userId = extractUserId(authentication);

        List<TopicResponse> topics;
        if (category != null && !category.isEmpty()) {
            topics = topicService.getTopicsByCategory(category, userId);
        } else {
            topics = topicService.getAllTopics(userId);
        }

        return ResponseEntity.ok(topics);
    }

    // Get topic by ID
    @GetMapping("/{id}")
    @Operation(summary = "Get topic by ID", description = "Retrieves a specific topic by its ID")
    public ResponseEntity<TopicResponse> getTopicById(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        TopicResponse response = topicService.getTopicById(id, userId);
        return ResponseEntity.ok(response);
    }

    // Update topic
    @PutMapping("/{id}")
    @Operation(summary = "Update topic", description = "Updates an existing topic")
    public ResponseEntity<TopicResponse> updateTopic(
            @PathVariable Long id,
            @Valid @RequestBody TopicRequest request,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        TopicResponse response = topicService.updateTopic(id, request, userId);
        return ResponseEntity.ok(response);
    }

    // Delete topic
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete topic", description = "Deletes a topic and all its associated data")
    public ResponseEntity<Void> deleteTopic(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        topicService.deleteTopic(id, userId);
        return ResponseEntity.noContent().build();
    }

    // Get root topics (topics with no parent)
    @GetMapping("/root")
    @Operation(summary = "Get root topics", description = "Retrieves all top-level topics (topics without a parent)")
    public ResponseEntity<List<TopicResponse>> getRootTopics(Authentication authentication) {
        Long userId = extractUserId(authentication);
        List<TopicResponse> topics = topicService.getRootTopics(userId);
        return ResponseEntity.ok(topics);
    }

    // Get child topics of a parent
    @GetMapping("/{id}/children")
    @Operation(summary = "Get child topics", description = "Retrieves all child topics of a specific parent topic")
    public ResponseEntity<List<TopicResponse>> getChildTopics(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        List<TopicResponse> topics = topicService.getChildTopics(id, userId);
        return ResponseEntity.ok(topics);
    }

    // Get at-risk topics (low confidence)
    @GetMapping("/at-risk")
    @Operation(summary = "Get at-risk topics", description = "Retrieves topics with low confidence levels")
    public ResponseEntity<List<TopicResponse>> getAtRiskTopics(
            @RequestParam(defaultValue = "3") Integer maxConfidence,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        List<TopicResponse> topics = topicService.getAtRiskTopics(userId, maxConfidence);
        return ResponseEntity.ok(topics);
    }

    // Get total topic count
    @GetMapping("/count")
    @Operation(summary = "Get topic count", description = "Returns the total number of topics for the authenticated user")
    public ResponseEntity<Long> getTopicCount(Authentication authentication) {
        Long userId = extractUserId(authentication);
        Long count = topicService.getTopicCount(userId);
        return ResponseEntity.ok(count);
    }

//    // Helper method to extract userId from JWT
//    private Long extractUserId(Authentication authentication) {
//        // Assuming your JWT stores userId as the principal
//        // Adjust this based on your JWT implementation
//        return Long.parseLong(authentication.getName());
//    }


    // If you have a custom User entity implementing UserDetails
    private Long extractUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        System.out.println(user.getId());
        return user.getId();
    }

//    // OR if you have a custom UserDetails class
//    private Long extractUserId(Authentication authentication) {
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        return userDetails.getUserId();
//    }
}