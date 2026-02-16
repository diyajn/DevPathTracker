package com.org.example.mapper;


import com.org.example.dto.TopicRequest;
import com.org.example.dto.TopicResponse;
import com.org.example.entities.Topic;
import org.springframework.stereotype.Component;

@Component
public class TopicMapper {

    // Convert Entity to Response DTO
    public TopicResponse toResponse(Topic topic) {
        return TopicResponse.builder()
                .id(topic.getId())
                .name(topic.getName())
                .category(topic.getCategory())
                .difficulty(topic.getDifficulty())
                .learnedDate(topic.getLearnedDate())
                .lastReviewed(topic.getLastReviewed())
                .confidenceLevel(topic.getConfidenceLevel())
                .parentTopicId(topic.getParentTopicId())
                .tags(topic.getTags())
                .createdAt(topic.getCreatedAt())
                .updatedAt(topic.getUpdatedAt())
                .build();
    }

    // Convert Entity to Response DTO with parent name
    public TopicResponse toResponse(Topic topic, String parentTopicName, Boolean hasChildren) {
        TopicResponse response = toResponse(topic);
        response.setParentTopicName(parentTopicName);
        response.setHasChildren(hasChildren);
        return response;
    }

    // Convert Request DTO to Entity (for creation)
    public Topic toEntity(TopicRequest request, Long userId) {
        Topic topic = new Topic();
        topic.setUserId(userId);
        topic.setName(request.getName());
        topic.setCategory(request.getCategory());
        topic.setDifficulty(request.getDifficulty());
        topic.setLearnedDate(request.getLearnedDate());
        topic.setParentTopicId(request.getParentTopicId());
        topic.setTags(request.getTags());

        // Set defaults
        topic.setConfidenceLevel(request.getConfidenceLevel() != null ?
                request.getConfidenceLevel() : 3);
        topic.setLastReviewed(request.getLearnedDate());

        return topic;
    }

    // Update existing entity with request data
    public void updateEntity(Topic topic, TopicRequest request) {
        if (request.getName() != null) {
            topic.setName(request.getName());
        }
        if (request.getCategory() != null) {
            topic.setCategory(request.getCategory());
        }
        if (request.getDifficulty() != null) {
            topic.setDifficulty(request.getDifficulty());
        }
        if (request.getLearnedDate() != null) {
            topic.setLearnedDate(request.getLearnedDate());
        }
        if (request.getParentTopicId() != null) {
            topic.setParentTopicId(request.getParentTopicId());
        }
        if (request.getTags() != null) {
            topic.setTags(request.getTags());
        }
        if (request.getConfidenceLevel() != null) {
            topic.setConfidenceLevel(request.getConfidenceLevel());
        }
    }
}
