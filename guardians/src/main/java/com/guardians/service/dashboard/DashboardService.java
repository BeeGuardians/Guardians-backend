package com.guardians.service.dashboard;

import com.guardians.dto.dashboard.ResRadarChartDto;
import java.util.List;

public interface DashboardService {
    List<ResRadarChartDto.CategoryScore> calculateRadarChart(Long userId);
}
