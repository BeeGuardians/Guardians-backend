package com.guardians.dto.board.res;

import com.guardians.domain.board.entity.Board;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class ResBoardListDto {
    private Long boardId;
    private String title;
    private String username;
    private int viewCount;
    private int likeCount;
    private LocalDateTime createdAt;
    private String boardType;
    private long commentCount;


    public static ResBoardListDto fromEntity(Board board) {
        return ResBoardListDto.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .username(board.getUser().getUsername())
                .createdAt(board.getCreatedAt()) // 날짜 형식 필요시 포맷팅
                .likeCount(board.getLikeCount())
                .viewCount(board.getViewCount())
                .boardType(board.getBoardType().name())
                .build();
    }

}
