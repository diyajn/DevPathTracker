package com.org.example.entities;

import com.org.example.enums.AutomationLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "skill_evolution")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillEvolution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "skill_family_id", nullable = false)
    private Long skillFamilyId;

    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    @Column(nullable = false)
    private Integer level;  // 1, 2, 3, 4...

    @Column(name = "complexity_score")
    private Integer complexityScore;  // 1-10 (10 = most complex)

    @Enumerated(EnumType.STRING)
    @Column(name = "automation_level", length = 20)
    private AutomationLevel automationLevel;

    @Column(columnDefinition = "TEXT")
    private String pros;

    @Column(columnDefinition = "TEXT")
    private String cons;

    @Column(name = "use_cases", columnDefinition = "TEXT")
    private String useCases;

    @Column(name = "code_comparison", columnDefinition = "TEXT")
    private String codeComparison;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
