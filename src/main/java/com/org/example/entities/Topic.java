package com.org.example.entities;

import com.org.example.enums.DifficultyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "topics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DifficultyType difficulty;

    @Column(name = "learned_date")
    private LocalDate learnedDate;

    @Column(name = "last_reviewed")
    private LocalDate lastReviewed;

    @Column(name = "confidence_level")
    private Integer confidenceLevel = 3;

    @Column(name = "parent_topic_id")
    private Long parentTopicId;

    @Column(length = 500)
    private String tags;

    // NEW: SM-2 Algorithm fields

    //- precision = 3 and scale = 2 means the column can store up to 3 digits in total,
    // with 2 digits after the decimal.
    @Column(name = "easiness_factor", precision = 3, scale = 2)
    private BigDecimal easinessFactor = BigDecimal.valueOf(2.5);

    @Column(name = "repetition_count")
    private Integer repetitionCount = 0;

    @Column(name = "last_interval_days")
    private Integer lastIntervalDays = 1;


    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
