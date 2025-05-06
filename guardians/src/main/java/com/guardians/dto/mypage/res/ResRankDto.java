package com.guardians.dto.mypage.res;

import com.guardians.domain.user.entity.UserStats;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResRankDto {
    private String username;      // ✅ username 추가됨
    private int score;
    private int totalSolved;
    private int rank;

    public static ResRankDto fromEntity(UserStats stats, int rank) {
        return ResRankDto.builder()
                .username(stats.getUser().getUsername())
                .score(stats.getScore())
                .totalSolved(stats.getTotalSolved())
                .rank(rank)
                .build();
    }
}

