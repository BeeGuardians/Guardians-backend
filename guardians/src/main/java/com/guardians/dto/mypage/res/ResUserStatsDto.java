package com.guardians.dto.mypage.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResUserStatsDto {
    private int score;
    private int rank;
    private String tier;
    private int solvedCount;
}