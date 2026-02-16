package com.org.example.service;


import com.org.example.dto.NoteRequest;
import com.org.example.dto.NoteResponse;
import com.org.example.entities.Note;
import com.org.example.entities.Topic;
import com.org.example.exception.ResourceNotFoundException;
import com.org.example.exception.UnauthorizedException;
import com.org.example.mapper.NoteMapper;
import com.org.example.repository.NoteRepository;
import com.org.example.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final TopicRepository topicRepository;
    private final NoteMapper noteMapper;

    // Create a note for a topic
    @Transactional
    public NoteResponse createNote(Long topicId, NoteRequest request, Long userId) {
        // Verify topic exists and belongs to user
        Topic topic = validateTopicOwnership(topicId, userId);

        Note note = noteMapper.toEntity(request, topicId);
        Note savedNote = noteRepository.save(note);

        return noteMapper.toResponse(savedNote, topic.getName());
    }

    // Get a note by ID
    public NoteResponse getNoteById(Long topicId, Long noteId, Long userId) {
        // Verify topic ownership
        Topic topic = validateTopicOwnership(topicId, userId);

        Note note = noteRepository.findByIdAndTopicId(noteId, topicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Note not found with id: " + noteId + " for topic: " + topicId));

        return noteMapper.toResponse(note, topic.getName());
    }

    // Get all notes for a topic
    public List<NoteResponse> getAllNotesForTopic(Long topicId, Long userId) {
        // Verify topic ownership
        Topic topic = validateTopicOwnership(topicId, userId);

        List<Note> notes = noteRepository.findByTopicId(topicId);

        return notes.stream()
                .map(note -> noteMapper.toResponse(note, topic.getName()))
                .collect(Collectors.toList());
    }

    // Update a note
    @Transactional
    public NoteResponse updateNote(Long topicId, Long noteId, NoteRequest request, Long userId) {
        // Verify topic ownership
        Topic topic = validateTopicOwnership(topicId, userId);

        Note note = noteRepository.findByIdAndTopicId(noteId, topicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Note not found with id: " + noteId + " for topic: " + topicId));

        noteMapper.updateEntity(note, request);
        Note updatedNote = noteRepository.save(note);

        return noteMapper.toResponse(updatedNote, topic.getName());
    }

    // Delete a note
    @Transactional
    public void deleteNote(Long topicId, Long noteId, Long userId) {
        // Verify topic ownership
        validateTopicOwnership(topicId, userId);

        Note note = noteRepository.findByIdAndTopicId(noteId, topicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Note not found with id: " + noteId + " for topic: " + topicId));

        noteRepository.delete(note);
    }

    // Search notes within a topic
    public List<NoteResponse> searchNotes(Long topicId, String keyword, Long userId) {
        // Verify topic ownership
        Topic topic = validateTopicOwnership(topicId, userId);

        List<Note> notes = noteRepository.searchNotesByKeyword(topicId, keyword);

        return notes.stream()
                .map(note -> noteMapper.toResponse(note, topic.getName()))
                .collect(Collectors.toList());
    }

    // Get notes by programming language
    public List<NoteResponse> getNotesByLanguage(Long topicId, String language, Long userId) {
        // Verify topic ownership
        Topic topic = validateTopicOwnership(topicId, userId);

        List<Note> notes = noteRepository.findByTopicIdAndLanguage(topicId, language);

        return notes.stream()
                .map(note -> noteMapper.toResponse(note, topic.getName()))
                .collect(Collectors.toList());
    }

    // Get note count for a topic
    public Long getNoteCount(Long topicId, Long userId) {
        // Verify topic ownership
        validateTopicOwnership(topicId, userId);

        return noteRepository.countByTopicId(topicId);
    }

    // Helper: Validate topic exists and belongs to user
    private Topic validateTopicOwnership(Long topicId, Long userId) {
        Topic topic = topicRepository.findByIdAndUserId(topicId, userId)
                .orElseThrow(() -> new UnauthorizedException(
                        "Topic not found or you don't have permission to access it"));
        return topic;
    }
}
