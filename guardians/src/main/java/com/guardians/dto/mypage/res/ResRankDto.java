package com.guardians.dto.mypage.res;

import com.guardians.domain.user.entity.UserStats;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResRankDto {
    private Long userId;
    private String username;
    private int score;
    private int totalSolved;
    private int rank;
    private String userProfileUrl;

    public static ResRankDto fromEntity(UserStats stats, int rank) {
        return ResRankDto.builder()
                .userId(stats.getUserId())
                .username(stats.getUser().getUsername())
                .score(stats.getScore())
                .totalSolved(stats.getTotalSolved())
                .rank(rank)
                .userProfileUrl(stats.getUser().getProfileImageUrl())
                .build();
    }
}

