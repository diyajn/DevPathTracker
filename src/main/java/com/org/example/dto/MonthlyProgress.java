package com.org.example.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyProgress {
    private String month;          // "2024-01"
    private String monthName;      // "January 2024"
    private Long topicsLearned;
    private Long reviewsCompleted;
}