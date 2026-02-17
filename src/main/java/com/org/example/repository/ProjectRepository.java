package com.org.example.repository;


import com.org.example.entities.Project;
import com.org.example.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Find all projects for a user
    List<Project> findByUserId(Long userId);

    // Find project by id and userId (ownership check)
    Optional<Project> findByIdAndUserId(Long id, Long userId);

    // Find projects by status
    List<Project> findByUserIdAndStatus(Long userId, ProjectStatus status);

    // Find projects that use a specific topic
    @Query("SELECT p FROM Project p JOIN p.topics t WHERE p.userId = :userId AND t.id = :topicId")
    List<Project> findProjectsByTopic(@Param("userId") Long userId, @Param("topicId") Long topicId);

    // Count projects for a user
    Long countByUserId(Long userId);

    // Count projects by status
    Long countByUserIdAndStatus(Long userId, ProjectStatus status);

    // Check if project exists for user
    boolean existsByIdAndUserId(Long id, Long userId);
}