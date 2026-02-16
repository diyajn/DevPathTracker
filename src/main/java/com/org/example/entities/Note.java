package com.org.example.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="topic_id",nullable = false)
    private Long topicId;

    @Column(length=255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name="code_snippet",columnDefinition = "TEXT")
    private String codeSnippet;

    @Column(length=50)
    private String language; //java,python.javascript,sql,etc.

    @Column(length = 500)
    private String tags;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
