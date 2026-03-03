package com.org.example.controller;


import com.org.example.dto.SearchRequest;
import com.org.example.dto.SearchResponse;
import com.org.example.entities.User;
import com.org.example.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "APIs for global search and filtering")
public class SearchController {

    private final SearchService searchService;

    // Global search (POST for complex filters)
    @PostMapping
    @Operation(summary = "Global search", description = "Search across topics, notes, and projects with filters")
    public ResponseEntity<SearchResponse> search(
            @RequestBody SearchRequest request,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        SearchResponse response = searchService.search(request, userId);
        return ResponseEntity.ok(response);
    }

    // Simple GET search (for quick queries)
    @GetMapping
    @Operation(summary = "Quick search", description = "Quick search with query parameter")
    public ResponseEntity<SearchResponse> quickSearch(
            @RequestParam String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            Authentication authentication) {

        Long userId = extractUserId(authentication);

        SearchRequest request = new SearchRequest();
        request.setQuery(q);
        request.setPage(page);
        request.setSize(size);

        SearchResponse response = searchService.search(request, userId);
        return ResponseEntity.ok(response);
    }

    // Helper method
    private Long extractUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        System.out.println(user.getId());
        return user.getId();
    }
}