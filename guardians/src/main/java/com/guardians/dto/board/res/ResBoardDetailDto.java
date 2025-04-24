package com.guardians.dto.board.res;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResBoardDetailDto {
    private Long boardId;
    private String title;
    private String content;
    private String username;
    private int viewCount;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
