package com.org.example.dto;

import com.org.example.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest{

    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 200, message = "Project name must be between 3 and 200 characters")
    private String name;

    private String description;

    @Size(max = 500, message = "GitHub URL cannot exceed 500 characters")
    private String githubUrl;

    private LocalDate startDate;

    private LocalDate endDate;

    private ProjectStatus status;

    // List of topic IDs to link with this project
    private List<Long> topicIds;
}
