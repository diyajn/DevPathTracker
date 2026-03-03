package com.org.example.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResultItem {

    private String type;           // "TOPIC", "NOTE", "PROJECT"
    private Long id;
    private String title;
    private String snippet;        // Preview of content
    private String category;
    private List<String> tags;
    private Integer confidenceLevel;
    private LocalDate learnedDate;
    private LocalDate lastReviewed;
    private Double relevanceScore; // How relevant to search query (0-1)
}
