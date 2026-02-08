package com.guardians.dto.board.res;

import com.guardians.domain.board.entity.Board;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResCreateBoardDto {
    private Long boardId;
    private String title;
    private String username;
    private String content;

    public static ResCreateBoardDto fromEntity(Board board) {
        return ResCreateBoardDto.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .username(board.getUser().getUsername())
                .build();
    }
}
