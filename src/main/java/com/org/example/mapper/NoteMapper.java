package com.org.example.mapper;

import com.org.example.dto.NoteRequest;
import com.org.example.dto.NoteResponse;
import com.org.example.entities.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    // Convert Entity to Response DTO
    public NoteResponse toResponse(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .topicId(note.getTopicId())
                .title(note.getTitle())
                .content(note.getContent())
                .codeSnippet(note.getCodeSnippet())
                .language(note.getLanguage())
                .tags(note.getTags())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }

    // Convert Entity to Response DTO with topic name
    public NoteResponse toResponse(Note note, String topicName) {
        NoteResponse response = toResponse(note);
        response.setTopicName(topicName);
        return response;
    }

    // Convert Request DTO to Entity
    public Note toEntity(NoteRequest request, Long topicId) {
        Note note = new Note();
        note.setTopicId(topicId);
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setCodeSnippet(request.getCodeSnippet());
        note.setLanguage(request.getLanguage());
        note.setTags(request.getTags());
        return note;
    }

    // Update existing entity with request data
    public void updateEntity(Note note, NoteRequest request) {
        if (request.getTitle() != null) {
            note.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            note.setContent(request.getContent());
        }
        if (request.getCodeSnippet() != null) {
            note.setCodeSnippet(request.getCodeSnippet());
        }
        if (request.getLanguage() != null) {
            note.setLanguage(request.getLanguage());
        }
        if (request.getTags() != null) {
            note.setTags(request.getTags());
        }
    }
}
