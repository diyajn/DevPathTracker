package com.org.example.mapper;


import com.org.example.dto.ProjectRequest;
import com.org.example.dto.ProjectResponse;
import com.org.example.entities.Project;
import com.org.example.enums.ProjectStatus;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectMapper {

    // Convert Entity to Response DTO
    public ProjectResponse toResponse(Project project) {
        Integer durationDays = null;
        if (project.getStartDate() != null && project.getEndDate() != null) {
            durationDays = (int) ChronoUnit.DAYS.between(
                    project.getStartDate(),
                    project.getEndDate()
            );
        }

        List<ProjectResponse.TopicSummary> topicSummaries = project.getTopics().stream()
                .map(topic -> ProjectResponse.TopicSummary.builder()
                        .id(topic.getId())
                        .name(topic.getName())
                        .category(topic.getCategory())
                        .build())
                .collect(Collectors.toList());

        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .githubUrl(project.getGithubUrl())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .status(project.getStatus())
                .durationDays(durationDays)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .topics(topicSummaries)
                .build();
    }

    // Convert Request DTO to Entity
    public Project toEntity(ProjectRequest request, Long userId) {
        Project project = new Project();
        project.setUserId(userId);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setGithubUrl(request.getGithubUrl());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setStatus(request.getStatus() != null ? request.getStatus() :
                ProjectStatus.PLANNING);
        return project;
    }

    // Update existing entity with request data
    public void updateEntity(Project project, ProjectRequest request) {
        if (request.getName() != null) {
            project.setName(request.getName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getGithubUrl() != null) {
            project.setGithubUrl(request.getGithubUrl());
        }
        if (request.getStartDate() != null) {
            project.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            project.setEndDate(request.getEndDate());
        }
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }
    }
}