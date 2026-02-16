package com.org.example.repository;

import com.org.example.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Find all notes for a topic
    List<Note> findByTopicId(Long topicId);

    // Find a specific note by id and topicId
    Optional<Note> findByIdAndTopicId(Long id, Long topicId);

    // Count notes for a topic
    Long countByTopicId(Long topicId);

    // Search notes by title or content (case-insensitive)
    @Query("SELECT n FROM Note n WHERE n.topicId = :topicId AND " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(n.codeSnippet) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Note> searchNotesByKeyword(@Param("topicId") Long topicId,
                                    @Param("keyword") String keyword);

    // Find notes by language
    List<Note> findByTopicIdAndLanguage(Long topicId, String language);

    // Delete all notes for a topic (cascades automatically, but can be explicit)
    void deleteByTopicId(Long topicId);
}
