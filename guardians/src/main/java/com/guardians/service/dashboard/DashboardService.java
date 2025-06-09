package com.guardians.service.dashboard;

import com.guardians.dto.dashboard.ResRadarChartDto;
import com.guardians.dto.dashboard.ResScoreTrendDto;
import com.guardians.dto.dashboard.ResSolvedTimelineDto;

import java.util.List;

public interface DashboardService {
    List<ResRadarChartDto.CategoryScore> calculateRadarChart(Long userId);

    List<ResSolvedTimelineDto> getSolvedTimeline(Long userId);

    List<ResScoreTrendDto> getScoreTrend(Long userId);
}
