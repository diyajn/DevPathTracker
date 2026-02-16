package com.org.example.repository;

import com.org.example.entities.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    // Find all topics for a user
    List<Topic> findByUserId(Long userId);

    // Find topics by user and category
    List<Topic> findByUserIdAndCategory(Long userId, String category);

    // Find topics with low confidence (for "at-risk" topics)
    List<Topic> findByUserIdAndConfidenceLevelLessThan(Long userId, Integer level);

    // Find child topics of a parent
    List<Topic> findByUserIdAndParentTopicId(Long userId, Long parentTopicId);

    // Find root topics (topics with no parent)
    List<Topic> findByUserIdAndParentTopicIdIsNull(Long userId);

    // Find a specific topic by id and userId (for ownership check)
    Optional<Topic> findByIdAndUserId(Long id, Long userId);

    // Count total topics for a user
    Long countByUserId(Long userId);

    // Check if topic exists for user
    boolean existsByIdAndUserId(Long id, Long userId);
}
