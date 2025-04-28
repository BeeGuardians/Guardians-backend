package com.guardians.dto.mypage.res;

import com.guardians.domain.user.entity.UserStats;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResRankDto {

    private int score;
    private int totalSolved;
    private int rank; // 나중에 랭킹계산 기능 넣으면 여기서 뽑을 수 있음

    public static ResRankDto fromEntity(UserStats stats) {
        return ResRankDto.builder()
                .score(stats.getScore())
                .totalSolved(stats.getTotalSolved())
                .rank(0) // 랭킹 계산 안했으면 기본 0
                .build();
    }
}

