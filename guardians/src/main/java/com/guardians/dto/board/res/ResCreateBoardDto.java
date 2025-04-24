package com.guardians.dto.board.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResCreateBoardDto {
    private Long boardId;
    private String title;
    private String username;
    private String content;
}
