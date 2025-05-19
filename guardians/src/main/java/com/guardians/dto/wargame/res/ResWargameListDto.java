package com.guardians.dto.wargame.res;

import com.guardians.domain.wargame.entity.Difficulty;
import com.guardians.domain.wargame.entity.Wargame;
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
    private int score;

    // 분류 및 난이도
    private Long category;
    private Difficulty difficulty;

    // 시간 정보
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 유저 기준 상태
    private boolean solved;
    private boolean bookmarked;
    private boolean liked;

    public static ResWargameListDto fromEntity(Wargame wargame, boolean solved, boolean bookmarked, boolean liked) {
        return ResWargameListDto.builder()
                .id(wargame.getId())
                .title(wargame.getTitle())
                .description(wargame.getDescription())
                .fileUrl(wargame.getFileUrl())
                .dockerImageUrl(wargame.getDockerImageUrl())
                .likeCount(wargame.getLikeCount())
                .score(wargame.getScore())
                .category(wargame.getCategory().getId())
                .difficulty(wargame.getDifficulty())
                .createdAt(wargame.getCreatedAt())
                .updatedAt(wargame.getUpdatedAt())
                .solved(solved)
                .bookmarked(bookmarked)
                .liked(liked)
                .build();
    }
}
