package com.org.example.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponse {

    private String query;
    private Long totalResults;
    private Integer currentPage;
    private Integer totalPages;
    private Integer pageSize;
    private List<SearchResultItem> results;
    private Long searchTimeMs;  // How long the search took
}
