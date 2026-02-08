package com.guardians.dto.board.res;

import com.guardians.domain.board.entity.Board;
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
    private String boardType;
    private Long userId;
    private boolean liked;

    public static ResBoardDetailDto fromEntity(Board board, boolean liked) {
        return ResBoardDetailDto.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .username(board.getUser().getUsername())
                .viewCount(board.getViewCount())
                .likeCount(board.getLikeCount())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .userId(board.getUser().getId())
                .boardType(board.getBoardType().name())
                .liked(liked)
                .build();
    }
}
