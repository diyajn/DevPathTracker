package com.org.example.service;


import com.org.example.dto.ProjectRequest;
import com.org.example.dto.ProjectResponse;
import com.org.example.entities.Project;
import com.org.example.entities.Topic;
import com.org.example.enums.ProjectStatus;
import com.org.example.exception.ResourceNotFoundException;
import com.org.example.exception.UnauthorizedException;
import com.org.example.mapper.ProjectMapper;
import com.org.example.repository.ProjectRepository;
import com.org.example.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TopicRepository topicRepository;
    private final ProjectMapper projectMapper;

    // Create a new project
    @Transactional
    public ProjectResponse createProject(ProjectRequest request, Long userId) {
        Project project = projectMapper.toEntity(request, userId);

        // Link topics if provided
        if (request.getTopicIds() != null && !request.getTopicIds().isEmpty()) {
            Set<Topic> topics = linkTopicsToProject(request.getTopicIds(), userId);
            topics.forEach(project::addTopic);
        }

        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponse(savedProject);
    }

    // Get project by ID
    public ProjectResponse getProjectById(Long id, Long userId) {
        Project project = projectRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        return projectMapper.toResponse(project);
    }

    // Get all projects for a user
    public List<ProjectResponse> getAllProjects(Long userId) {
        List<Project> projects = projectRepository.findByUserId(userId);
        return projects.stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get projects by status
    public List<ProjectResponse> getProjectsByStatus(ProjectStatus status, Long userId) {
        List<Project> projects = projectRepository.findByUserIdAndStatus(userId, status);
        return projects.stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get projects that use a specific topic
    public List<ProjectResponse> getProjectsByTopic(Long topicId, Long userId) {
        // Verify topic belongs to user
        validateTopicOwnership(topicId, userId);

        List<Project> projects = projectRepository.findProjectsByTopic(userId, topicId);
        return projects.stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Update project
    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request, Long userId) {
        Project project = projectRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        projectMapper.updateEntity(project, request);

        // Update topics if provided
        if (request.getTopicIds() != null) {
            project.clearTopics();
            Set<Topic> topics = linkTopicsToProject(request.getTopicIds(), userId);
            topics.forEach(project::addTopic);
        }

        Project updatedProject = projectRepository.save(project);
        return projectMapper.toResponse(updatedProject);
    }

    // Delete project
    @Transactional
    public void deleteProject(Long id, Long userId) {
        Project project = projectRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        projectRepository.delete(project);
    }

    // Add topics to existing project
    @Transactional
    public ProjectResponse addTopicsToProject(Long projectId, List<Long> topicIds, Long userId) {
        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        Set<Topic> topics = linkTopicsToProject(topicIds, userId);
        topics.forEach(project::addTopic);

        Project updatedProject = projectRepository.save(project);
        return projectMapper.toResponse(updatedProject);
    }

    // Remove topic from project
    @Transactional
    public ProjectResponse removeTopicFromProject(Long projectId, Long topicId, Long userId) {
        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        Topic topic = topicRepository.findByIdAndUserId(topicId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + topicId));

        project.removeTopic(topic);
        Project updatedProject = projectRepository.save(project);
        return projectMapper.toResponse(updatedProject);
    }

    // Get project count
    public Long getProjectCount(Long userId) {
        return projectRepository.countByUserId(userId);
    }

    // Get project count by status
    public Long getProjectCountByStatus(ProjectStatus status, Long userId) {
        return projectRepository.countByUserIdAndStatus(userId, status);
    }

    // Helper: Link topics to project
    private Set<Topic> linkTopicsToProject(List<Long> topicIds, Long userId) {
        Set<Topic> topics = new HashSet<>();

        for (Long topicId : topicIds) {
            Topic topic = topicRepository.findByIdAndUserId(topicId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Topic not found with id: " + topicId));
            topics.add(topic);
        }

        return topics;
    }

    // Helper: Validate topic ownership
    private void validateTopicOwnership(Long topicId, Long userId) {
        if (!topicRepository.existsByIdAndUserId(topicId, userId)) {
            throw new UnauthorizedException("Topic not found or you don't have permission");
        }
    }
}