package com.guardians.dto.mypage.res;

import com.guardians.domain.board.entity.Board;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ResBoardDto {

    private List<BoardInfo> boards;

    @Getter
    @Builder
    public static class BoardInfo {
        private Long boardId;
        private String title;
        private String content;
        private String boardType;
    }

    public static ResBoardDto fromEntities(List<Board> boardList) {
        return ResBoardDto.builder()
                .boards(
                        boardList.stream()
                                .map(board -> BoardInfo.builder()
                                        .boardId(board.getId())
                                        .title(board.getTitle())
                                        .content(board.getContent())
                                        .boardType(board.getBoardType().toString())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }
}

