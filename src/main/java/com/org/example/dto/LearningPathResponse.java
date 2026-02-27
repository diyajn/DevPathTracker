package com.org.example.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningPathResponse {

    private Long id;
    private Integer sequenceNumber;
    private Long fromTopicId;
    private String fromTopicName;
    private Long toTopicId;
    private String toTopicName;
    private LocalDateTime createdAt;
}