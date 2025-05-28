package com.guardians.dto.dashboard;

import com.guardians.domain.wargame.entity.SolvedWargame;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResSolvedTimelineDto {
    private String category;
    private String name;
    private String date; // yyyy-MM-dd

    public static ResSolvedTimelineDto fromEntity(SolvedWargame solved) {
        return ResSolvedTimelineDto.builder()
                .category(solved.getWargame().getCategory().getName())
                .name(solved.getWargame().getTitle())
                .date(solved.getSolvedAt().toLocalDate().toString())
                .build();
    }
}