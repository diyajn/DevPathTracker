package com.org.example.controller;

import com.org.example.dto.NoteRequest;
import com.org.example.dto.NoteResponse;
import com.org.example.entities.User;
import com.org.example.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics/{topicId}/notes")
@RequiredArgsConstructor
@Tag(name = "Note Management", description = "APIs for managing notes and code snippets")
public class NoteController {

    private final NoteService noteService;

    // Create a note for a topic
    @PostMapping
    @Operation(summary = "Create a note", description = "Creates a new note with optional code snippet for a topic")
    public ResponseEntity<NoteResponse> createNote(
            @PathVariable Long topicId,
            @Valid @RequestBody NoteRequest request,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        NoteResponse response = noteService.createNote(topicId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all notes for a topic
    @GetMapping
    @Operation(summary = "Get all notes for a topic", description = "Retrieves all notes associated with a specific topic")
    public ResponseEntity<List<NoteResponse>> getAllNotesForTopic(
            @PathVariable Long topicId,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        List<NoteResponse> notes = noteService.getAllNotesForTopic(topicId, userId);
        return ResponseEntity.ok(notes);
    }

    // Get a specific note
    @GetMapping("/{noteId}")
    @Operation(summary = "Get note by ID", description = "Retrieves a specific note by its ID")
    public ResponseEntity<NoteResponse> getNoteById(
            @PathVariable Long topicId,
            @PathVariable Long noteId,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        NoteResponse response = noteService.getNoteById(topicId, noteId, userId);
        return ResponseEntity.ok(response);
    }

    // Update a note
    @PutMapping("/{noteId}")
    @Operation(summary = "Update a note", description = "Updates an existing note")
    public ResponseEntity<NoteResponse> updateNote(
            @PathVariable Long topicId,
            @PathVariable Long noteId,
            @Valid @RequestBody NoteRequest request,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        NoteResponse response = noteService.updateNote(topicId, noteId, request, userId);
        return ResponseEntity.ok(response);
    }

    // Delete a note
    @DeleteMapping("/{noteId}")
    @Operation(summary = "Delete a note", description = "Deletes a note from a topic")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long topicId,
            @PathVariable Long noteId,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        noteService.deleteNote(topicId, noteId, userId);
        return ResponseEntity.noContent().build();
    }

    // Search notes within a topic
    @GetMapping("/search")
    @Operation(summary = "Search notes", description = "Searches notes by keyword in title, content, or code snippet")
    public ResponseEntity<List<NoteResponse>> searchNotes(
            @PathVariable Long topicId,
            @RequestParam String keyword,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        List<NoteResponse> notes = noteService.searchNotes(topicId, keyword, userId);
        return ResponseEntity.ok(notes);
    }

    // Get notes by programming language
    @GetMapping("/language/{language}")
    @Operation(summary = "Get notes by language", description = "Retrieves notes filtered by programming language")
    public ResponseEntity<List<NoteResponse>> getNotesByLanguage(
            @PathVariable Long topicId,
            @PathVariable String language,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        List<NoteResponse> notes = noteService.getNotesByLanguage(topicId, language, userId);
        return ResponseEntity.ok(notes);
    }

    // Get note count for a topic
    @GetMapping("/count")
    @Operation(summary = "Get note count", description = "Returns the total number of notes for a topic")
    public ResponseEntity<Long> getNoteCount(
            @PathVariable Long topicId,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        Long count = noteService.getNoteCount(topicId, userId);
        return ResponseEntity.ok(count);
    }

    // Helper method to extract userId from JWT
    private Long extractUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        System.out.println(user.getId());
        return user.getId();
    }
}