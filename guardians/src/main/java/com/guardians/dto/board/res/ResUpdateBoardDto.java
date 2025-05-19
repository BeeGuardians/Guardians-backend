package com.guardians.dto.board.res;

import lombok.Builder;
import lombok.Getter;
import org.springframework.transaction.annotation.Transactional;

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
}