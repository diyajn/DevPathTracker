package com.org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoteResponse {
    private Long id;
    private Long topicId;
    private String topicName;  // Include topic name for context
    private String title;
    private String content;
    private String codeSnippet;
    private String language;
    private String tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
