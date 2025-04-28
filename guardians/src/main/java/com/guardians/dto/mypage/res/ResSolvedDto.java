package com.guardians.dto.mypage.res;

import com.guardians.domain.wargame.entity.SolvedWargame;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ResSolvedDto {

    private List<SolvedInfo> solvedList;

    @Getter
    @Builder
    public static class SolvedInfo {
        private Long wargameId;
        private String title;
        private String category;
        private Integer score;
    }

    public static ResSolvedDto fromEntities(List<SolvedWargame> solvedList) {
        return ResSolvedDto.builder()
                .solvedList(
                        solvedList.stream()
                                .map(solved -> SolvedInfo.builder()
                                        .wargameId(solved.getWargame().getId())
                                        .title(solved.getWargame().getTitle())
                                        .category(solved.getWargame().getCategory().getName())
                                        .score(solved.getWargame().getScore())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }
}
