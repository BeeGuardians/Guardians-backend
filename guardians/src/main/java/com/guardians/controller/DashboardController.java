package com.guardians.controller;

import com.guardians.dto.common.ResWrapper;
import com.guardians.dto.dashboard.ResRadarChartDto;
import com.guardians.dto.dashboard.ResSolvedTimelineDto;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import com.guardians.service.dashboard.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard API", description = "사용자별 대시보드 통계 API")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "카테고리별 정규화 점수 조회", description = "레이더 차트용 실력 점수를 조회합니다.")
    @GetMapping("/radar")
    public ResponseEntity<ResWrapper<?>> getRadarChart(
            @PathVariable Long userId,
            HttpSession session
    ) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (!userId.equals(sessionUserId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        List<ResRadarChartDto.CategoryScore> result = dashboardService.calculateRadarChart(userId);
        return ResponseEntity.ok(ResWrapper.resList("카테고리별 실력 점수 조회 성공", result, result.size()));
    }

    @Operation(summary = "사용자의 문제 풀이 타임라인 조회")
    @GetMapping("/timeline")
    public ResponseEntity<ResWrapper<?>> getSolvedTimeline(
            @PathVariable Long userId,
            HttpSession session
    ) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (!userId.equals(sessionUserId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        List<ResSolvedTimelineDto> result = dashboardService.getSolvedTimeline(userId);
        return ResponseEntity.ok(ResWrapper.resList("풀이 타임라인 조회 성공", result, result.size()));
    }

    @Operation(summary = "사용자의 날짜별 점수 획득 추이 조회", description = "문제 풀이 기록을 기반으로 날짜별 획득 점수를 반환합니다.")
    @GetMapping("/score-trend")
    public ResponseEntity<ResWrapper<?>> getScoreTrend(
            @PathVariable Long userId,
            HttpSession session
    ) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (!userId.equals(sessionUserId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        var result = dashboardService.getScoreTrend(userId);
        return ResponseEntity.ok(ResWrapper.resList("점수 추이 조회 성공", result, result.size()));
    }
}