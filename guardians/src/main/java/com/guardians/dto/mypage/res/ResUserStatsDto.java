package com.guardians.dto.mypage.res;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResUserStatsDto {
    private int score;
    private int rank;
    private int solvedCount;
}