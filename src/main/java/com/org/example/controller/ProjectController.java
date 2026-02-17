package com.org.example.controller;


import com.org.example.dto.ProjectRequest;
import com.org.example.dto.ProjectResponse;
import com.org.example.entities.User;
import com.org.example.enums.ProjectStatus;
import com.org.example.service.ProjectService;
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
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Project Management", description = "APIs for managing projects and linking them to topics")
public class ProjectController {

    private final ProjectService projectService;

    // Create a new project
    @PostMapping
    @Operation(summary = "Create a project", description = "Creates a new project and optionally links topics")
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        ProjectResponse response = projectService.createProject(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all projects
    @GetMapping
    @Operation(summary = "Get all projects", description = "Retrieves all projects for the authenticated user")
    public ResponseEntity<List<ProjectResponse>> getAllProjects(
            @RequestParam(required = false) ProjectStatus status,
            Authentication authentication) {

        Long userId = extractUserId(authentication);

        List<ProjectResponse> projects;
        if (status != null) {
            projects = projectService.getProjectsByStatus(status, userId);
        } else {
            projects = projectService.getAllProjects(userId);
        }

        return ResponseEntity.ok(projects);
    }

    // Get project by ID
    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID", description = "Retrieves a specific project with linked topics")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        ProjectResponse response = projectService.getProjectById(id, userId);
        return ResponseEntity.ok(response);
    }

    // Update project
    @PutMapping("/{id}")
    @Operation(summary = "Update project", description = "Updates project details and topic associations")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        ProjectResponse response = projectService.updateProject(id, request, userId);
        return ResponseEntity.ok(response);
    }

    // Delete project
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete project", description = "Deletes a project")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        projectService.deleteProject(id, userId);
        return ResponseEntity.noContent().build();
    }

    // Get projects by topic
    @GetMapping("/by-topic/{topicId}")
    @Operation(summary = "Get projects by topic", description = "Retrieves all projects that use a specific topic")
    public ResponseEntity<List<ProjectResponse>> getProjectsByTopic(
            @PathVariable Long topicId,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        List<ProjectResponse> projects = projectService.getProjectsByTopic(topicId, userId);
        return ResponseEntity.ok(projects);
    }

    // Add topics to project
    @PostMapping("/{id}/topics")
    @Operation(summary = "Add topics to project", description = "Links additional topics to an existing project")
    public ResponseEntity<ProjectResponse> addTopicsToProject(
            @PathVariable Long id,
            @RequestBody List<Long> topicIds,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        ProjectResponse response = projectService.addTopicsToProject(id, topicIds, userId);
        return ResponseEntity.ok(response);
    }

    // Remove topic from project
    @DeleteMapping("/{projectId}/topics/{topicId}")
    @Operation(summary = "Remove topic from project", description = "Unlinks a topic from a project")
    public ResponseEntity<ProjectResponse> removeTopicFromProject(
            @PathVariable Long projectId,
            @PathVariable Long topicId,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        ProjectResponse response = projectService.removeTopicFromProject(projectId, topicId, userId);
        return ResponseEntity.ok(response);
    }

    // Get project count
    @GetMapping("/count")
    @Operation(summary = "Get project count", description = "Returns total project count")
    public ResponseEntity<Long> getProjectCount(Authentication authentication) {
        Long userId = extractUserId(authentication);
        Long count = projectService.getProjectCount(userId);
        return ResponseEntity.ok(count);
    }

    // Helper method
    private Long extractUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        System.out.println(user.getId());
        return user.getId();
    }
}