package com.guardians.dto.dashboard;

import lombok.*;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResRadarChartDto {
    private List<CategoryScore> scores;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CategoryScore {
        private String category;
        private double normalizedScore;
    }
}
