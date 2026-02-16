package com.org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteRequest {
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String codeSnippet;

    @Size(max = 50, message = "Language cannot exceed 50 characters")
    private String language;  // java, python, javascript, etc.

    @Size(max = 500, message = "Tags cannot exceed 500 characters")
    private String tags;
}
