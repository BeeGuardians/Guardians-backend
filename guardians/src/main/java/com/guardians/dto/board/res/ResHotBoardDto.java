package com.guardians.dto.board.res;

import com.guardians.domain.board.entity.Board;
import com.guardians.domain.board.entity.BoardType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResHotBoardDto {
    private Long id;
    private String title;
    private BoardType boardType;
    private int likeCount;
    private int viewCount;
    private int score;

    public static ResHotBoardDto fromEntity(Board board) {
        return ResHotBoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .boardType(board.getBoardType())
                .likeCount(board.getLikeCount())
                .viewCount(board.getViewCount())
                .score(board.getLikeCount() * 2 + board.getViewCount())
                .build();
    }
}
