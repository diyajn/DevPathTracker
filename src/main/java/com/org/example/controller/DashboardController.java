package com.org.example.controller;

import com.org.example.dto.DashboardStatsResponse;
import com.org.example.dto.StreakInfo;
import com.org.example.entities.User;
import com.org.example.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "APIs for dashboard statistics and analytics")
public class DashboardController {

    private final DashboardService dashboardService;

    // Get complete dashboard stats
    @GetMapping
    @Operation(summary = "Get dashboard stats", description = "Retrieves comprehensive dashboard statistics")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats(Authentication authentication) {
        Long userId = extractUserId(authentication);
        DashboardStatsResponse stats = dashboardService.getDashboardStats(userId);
        return ResponseEntity.ok(stats);
    }

    // Get streak information
    @GetMapping("/streak")
    @Operation(summary = "Get streak info", description = "Retrieves current and longest review streaks")
    public ResponseEntity<StreakInfo> getStreakInfo(Authentication authentication) {
        Long userId = extractUserId(authentication);
        StreakInfo streakInfo = dashboardService.getStreakInfo(userId);
        return ResponseEntity.ok(streakInfo);
    }

    // Helper method
    private Long extractUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        System.out.println(user.getId());
        return user.getId();
    }
}