package com.guardians.dto.board.res;

import com.guardians.domain.board.entity.Board;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder

public class ResUpdateBoardDto {
    private Long boardId;
    private String title;
    private String content;
    private String username;
    private LocalDateTime updatedAt;
    private String boardType;

    public static ResUpdateBoardDto fromEntity(Board board) {
        return ResUpdateBoardDto.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .username(board.getUser().getUsername())
                .updatedAt(board.getUpdatedAt())
                .boardType(board.getBoardType().name())
                .build();
    }
}