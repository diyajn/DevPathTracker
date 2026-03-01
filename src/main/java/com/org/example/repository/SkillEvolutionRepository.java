package com.org.example.repository;

import com.org.example.entities.SkillEvolution;
import com.org.example.enums.AutomationLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillEvolutionRepository extends JpaRepository<SkillEvolution, Long> {

  // Find all evolutions in a skill family (ordered by level)
  List<SkillEvolution> findBySkillFamilyIdOrderByLevelAsc(Long skillFamilyId);

  // Find evolution by topic
  Optional<SkillEvolution> findByTopicId(Long topicId);

  // Find evolutions by automation level
  List<SkillEvolution> findByAutomationLevel(AutomationLevel automationLevel);

  // Find the highest level in a family
  @Query("SELECT MAX(se.level) FROM SkillEvolution se WHERE se.skillFamilyId = :familyId")
  Integer findMaxLevelInFamily(@Param("familyId") Long familyId);

  // Count evolutions in a family
  Long countBySkillFamilyId(Long skillFamilyId);

  // Check if topic is already in a family
  boolean existsByTopicId(Long topicId);
}