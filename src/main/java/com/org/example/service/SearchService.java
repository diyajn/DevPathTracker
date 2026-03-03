package com.org.example.service;


import com.org.example.dto.SearchRequest;
import com.org.example.dto.SearchResponse;
import com.org.example.dto.SearchResultItem;
import com.org.example.entities.Note;
import com.org.example.entities.Project;
import com.org.example.entities.Topic;
import com.org.example.repository.NoteRepository;
import com.org.example.repository.ProjectRepository;
import com.org.example.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final TopicRepository topicRepository;
    private final NoteRepository noteRepository;
    private final ProjectRepository projectRepository;

    /**
     * Global search across topics, notes, and projects
     */
    public SearchResponse search(SearchRequest request, Long userId) {
        long startTime = System.currentTimeMillis();

        // Get all results
        List<SearchResultItem> allResults = new ArrayList<>();

        // Search topics
        allResults.addAll(searchTopics(request, userId));

        // Search notes
        if (request.getQuery() != null && !request.getQuery().isEmpty()) {
            allResults.addAll(searchNotes(request, userId));
        }

        // Search projects
        if (request.getQuery() != null && !request.getQuery().isEmpty()) {
            allResults.addAll(searchProjects(request, userId));
        }

        // Apply filters
        allResults = applyFilters(allResults, request);

        // Sort results
        allResults = sortResults(allResults, request);

        // Calculate pagination
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;
        int totalResults = allResults.size();
        int totalPages = (int) Math.ceil((double) totalResults / size);

        // Paginate
        int start = page * size;
        int end = Math.min(start + size, totalResults);
        List<SearchResultItem> paginatedResults = start < totalResults
                ? allResults.subList(start, end)
                : new ArrayList<>();

        long endTime = System.currentTimeMillis();

        return SearchResponse.builder()
                .query(request.getQuery())
                .totalResults((long) totalResults)
                .currentPage(page)
                .totalPages(totalPages)
                .pageSize(size)
                .results(paginatedResults)
                .searchTimeMs(endTime - startTime)
                .build();
    }

    /**
     * Search in topics
     */
    private List<SearchResultItem> searchTopics(SearchRequest request, Long userId) {
        List<Topic> topics = topicRepository.findByUserId(userId);

        return topics.stream()
                .filter(topic -> matchesQuery(topic, request.getQuery()))
                .map(topic -> SearchResultItem.builder()
                        .type("TOPIC")
                        .id(topic.getId())
                        .title(topic.getName())
                        .snippet(createSnippet(topic.getName(), request.getQuery(), 100))
                        .category(topic.getCategory())
                        .tags(parseTags(topic.getTags()))
                        .confidenceLevel(topic.getConfidenceLevel())
                        .learnedDate(topic.getLearnedDate())
                        .lastReviewed(topic.getLastReviewed())
                        .relevanceScore(calculateRelevance(topic.getName(), request.getQuery()))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Search in notes
     */
    private List<SearchResultItem> searchNotes(SearchRequest request, Long userId) {
        List<Topic> userTopics = topicRepository.findByUserId(userId);
        List<SearchResultItem> noteResults = new ArrayList<>();

        for (Topic topic : userTopics) {
            List<Note> notes = noteRepository.findByTopicId(topic.getId());

            notes.stream()
                    .filter(note -> matchesQuery(note, request.getQuery()))
                    .forEach(note -> {
                        String searchText = note.getTitle() + " " + note.getContent() + " " + note.getCodeSnippet();

                        noteResults.add(SearchResultItem.builder()
                                .type("NOTE")
                                .id(note.getId())
                                .title(note.getTitle() != null ? note.getTitle() : "Untitled Note")
                                .snippet(createSnippet(searchText, request.getQuery(), 150))
                                .category(topic.getCategory())
                                .tags(parseTags(note.getTags()))
                                .confidenceLevel(topic.getConfidenceLevel())
                                .relevanceScore(calculateRelevance(searchText, request.getQuery()))
                                .build());
                    });
        }

        return noteResults;
    }

    /**
     * Search in projects
     */
    private List<SearchResultItem> searchProjects(SearchRequest request, Long userId) {
        List<Project> projects = projectRepository.findByUserId(userId);

        return projects.stream()
                .filter(project -> matchesQuery(project, request.getQuery()))
                .map(project -> {
                    String searchText = project.getName() + " " + project.getDescription();

                    return SearchResultItem.builder()
                            .type("PROJECT")
                            .id(project.getId())
                            .title(project.getName())
                            .snippet(createSnippet(searchText, request.getQuery(), 150))
                            .relevanceScore(calculateRelevance(searchText, request.getQuery()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Check if topic matches query
     */
    private boolean matchesQuery(Topic topic, String query) {
        if (query == null || query.isEmpty()) {
            return true;
        }

        String lowerQuery = query.toLowerCase();
        String searchText = (topic.getName() + " " +
                (topic.getCategory() != null ? topic.getCategory() : "") + " " +
                (topic.getTags() != null ? topic.getTags() : "")).toLowerCase();

        return searchText.contains(lowerQuery);
    }

    /**
     * Check if note matches query
     */
    private boolean matchesQuery(Note note, String query) {
        if (query == null || query.isEmpty()) {
            return true;
        }

        String lowerQuery = query.toLowerCase();
        String searchText = ((note.getTitle() != null ? note.getTitle() : "") + " " +
                (note.getContent() != null ? note.getContent() : "") + " " +
                (note.getCodeSnippet() != null ? note.getCodeSnippet() : "") + " " +
                (note.getTags() != null ? note.getTags() : "")).toLowerCase();

        return searchText.contains(lowerQuery);
    }

    /**
     * Check if project matches query
     */
    private boolean matchesQuery(Project project, String query) {
        if (query == null || query.isEmpty()) {
            return true;
        }

        String lowerQuery = query.toLowerCase();
        String searchText = (project.getName() + " " +
                (project.getDescription() != null ? project.getDescription() : "")).toLowerCase();

        return searchText.contains(lowerQuery);
    }

    /**
     * Apply filters to results
     */
    private List<SearchResultItem> applyFilters(List<SearchResultItem> results, SearchRequest request) {
        return results.stream()
                .filter(item -> {
                    // Category filter
                    if (request.getCategories() != null && !request.getCategories().isEmpty()) {
                        if (item.getCategory() == null ||
                                !request.getCategories().contains(item.getCategory())) {
                            return false;
                        }
                    }

                    // Tag filter
                    if (request.getTags() != null && !request.getTags().isEmpty()) {
                        if (item.getTags() == null || item.getTags().isEmpty()) {
                            return false;
                        }
                        boolean hasAnyTag = item.getTags().stream()
                                .anyMatch(tag -> request.getTags().contains(tag));
                        if (!hasAnyTag) {
                            return false;
                        }
                    }

                    // Confidence filter
                    if (request.getMinConfidence() != null && item.getConfidenceLevel() != null) {
                        if (item.getConfidenceLevel() < request.getMinConfidence()) {
                            return false;
                        }
                    }

                    if (request.getMaxConfidence() != null && item.getConfidenceLevel() != null) {
                        if (item.getConfidenceLevel() > request.getMaxConfidence()) {
                            return false;
                        }
                    }

                    // Date range filter
                    if (request.getLearnedAfter() != null && item.getLearnedDate() != null) {
                        if (item.getLearnedDate().isBefore(request.getLearnedAfter())) {
                            return false;
                        }
                    }

                    if (request.getLearnedBefore() != null && item.getLearnedDate() != null) {
                        if (item.getLearnedDate().isAfter(request.getLearnedBefore())) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * Sort results
     */
    private List<SearchResultItem> sortResults(List<SearchResultItem> results, SearchRequest request) {
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "relevance";
        String sortOrder = request.getSortOrder() != null ? request.getSortOrder() : "desc";

        Comparator<SearchResultItem> comparator;

        switch (sortBy.toLowerCase()) {
            case "name":
                comparator = Comparator.comparing(SearchResultItem::getTitle,
                        Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "learned_date":
                comparator = Comparator.comparing(SearchResultItem::getLearnedDate,
                        Comparator.nullsLast(LocalDate::compareTo));
                break;
            case "confidence":
                comparator = Comparator.comparing(SearchResultItem::getConfidenceLevel,
                        Comparator.nullsLast(Integer::compareTo));
                break;
            case "last_reviewed":
                comparator = Comparator.comparing(SearchResultItem::getLastReviewed,
                        Comparator.nullsLast(LocalDate::compareTo));
                break;
            case "relevance":
            default:
                comparator = Comparator.comparing(SearchResultItem::getRelevanceScore,
                        Comparator.nullsLast(Double::compareTo));
                break;
        }

        if ("asc".equalsIgnoreCase(sortOrder)) {
            return results.stream().sorted(comparator).collect(Collectors.toList());
        } else {
            return results.stream().sorted(comparator.reversed()).collect(Collectors.toList());
        }
    }

    /**
     * Calculate relevance score (0-1)
     */
    private Double calculateRelevance(String text, String query) {
        if (query == null || query.isEmpty() || text == null) {
            return 0.5; // Neutral relevance
        }

        String lowerText = text.toLowerCase();
        String lowerQuery = query.toLowerCase();

        // Exact match = highest score
        if (lowerText.equals(lowerQuery)) {
            return 1.0;
        }

        // Starts with query = high score
        if (lowerText.startsWith(lowerQuery)) {
            return 0.9;
        }

        // Contains query = medium score
        if (lowerText.contains(lowerQuery)) {
            // More occurrences = higher score
            int occurrences = countOccurrences(lowerText, lowerQuery);
            return Math.min(0.5 + (occurrences * 0.1), 0.8);
        }

        // No match = low score
        return 0.1;
    }

    /**
     * Count occurrences of query in text
     */
    private int countOccurrences(String text, String query) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(query, index)) != -1) {
            count++;
            index += query.length();
        }
        return count;
    }

    /**
     * Create snippet with highlighted query
     */
    private String createSnippet(String text, String query, int maxLength) {
        if (text == null) {
            return "";
        }

        if (text.length() <= maxLength) {
            return text;
        }

        // If query exists, try to center snippet around it
        if (query != null && !query.isEmpty()) {
            int queryIndex = text.toLowerCase().indexOf(query.toLowerCase());
            if (queryIndex != -1) {
                int start = Math.max(0, queryIndex - maxLength / 2);
                int end = Math.min(text.length(), start + maxLength);

                String snippet = text.substring(start, end);
                if (start > 0) snippet = "..." + snippet;
                if (end < text.length()) snippet = snippet + "...";

                return snippet;
            }
        }

        // Otherwise, just return first maxLength characters
        return text.substring(0, maxLength) + "...";
    }

    /**
     * Parse comma-separated tags into list
     */
    private List<String> parseTags(String tags) {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toList());
    }
}
