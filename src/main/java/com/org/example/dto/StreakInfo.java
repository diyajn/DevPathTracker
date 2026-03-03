package com.org.example.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StreakInfo {
    private Integer currentStreak;
    private Integer longestStreak;
    private LocalDate lastReviewDate;
    private Boolean reviewedToday;
}
