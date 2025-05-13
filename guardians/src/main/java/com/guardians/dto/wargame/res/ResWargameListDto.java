package com.guardians.dto.wargame.res;

import com.guardians.domain.wargame.entity.Difficulty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResWargameListDto {

    // 기본 정보
    private Long id;
    private String title;
    private String description;
    private String fileUrl;
    private String dockerImageUrl;
    private int likeCount;

    // 분류 및 난이도
    private Long category;

    private Difficulty difficulty;

    // 시간 정보
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 유저 기준 상태
    private boolean solved;  // 현재 로그인한 유저가 이 문제 풀었는지

    // 유저 북마크 상태
    private boolean bookmarked;

    // 유저 좋아요 상태
    private boolean liked;

}
