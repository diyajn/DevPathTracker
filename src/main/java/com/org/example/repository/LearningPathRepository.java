package com.org.example.repository;

import com.org.example.entities.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath,Long> {
    // Find all learning path entries for a user (ordered by sequence)
    List<LearningPath> findByUserIdOrderBySequenceNumberAsc(Long userId);

    // Find a specific path entry by topic
    Optional<LearningPath> findByUserIdAndToTopicId(Long userId, Long toTopicId);

    // Get the latest sequence number for a user
    @Query("SELECT MAX(lp.sequenceNumber) FROM LearningPath lp WHERE lp.userId = :userId")
    Integer findMaxSequenceNumber(@Param("userId") Long userId);

    // Count total entries in learning path
    Long countByUserId(Long userId);

    // Delete learning path entry by topic
    void deleteByUserIdAndToTopicId(Long userId, Long toTopicId);

}
