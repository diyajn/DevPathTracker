package com.org.example.repository;

import com.org.example.entities.SkillFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillFamilyRepository extends JpaRepository<SkillFamily, Long> {

  // Find by category
  List<SkillFamily> findByCategory(String category);

  // Find by name (case-insensitive)
  List<SkillFamily> findByNameContainingIgnoreCase(String name);
}