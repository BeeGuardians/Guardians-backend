package com.guardians.dto.board.res;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder

public class ResUpdateBoardDto {
    private Long boardId;
    private String title;
    private String content;
    private String username;
    private LocalDateTime updatedAt;
}