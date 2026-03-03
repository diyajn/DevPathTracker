package com.org.example.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    // Search query
    private String query;  // Search in name, content, code

    // Filters
    private List<String> categories;      // Filter by categories
    private List<String> tags;            // Filter by tags
    private Integer minConfidence;        // Confidence >= this value
    private Integer maxConfidence;        // Confidence <= this value
    private LocalDate learnedAfter;       // Learned after this date
    private LocalDate learnedBefore;      // Learned before this date
    private Boolean onlyDueForReview;     // Only topics due for review
    private Boolean onlyAtRisk;           // Only topics at risk (not reviewed 90+ days)

    // Sort options
    private String sortBy;    // "name", "learned_date", "confidence", "last_reviewed"
    private String sortOrder; // "asc" or "desc"

    // Pagination
    private Integer page;     // Page number (0-based)
    private Integer size;     // Page size (default 20)
}
