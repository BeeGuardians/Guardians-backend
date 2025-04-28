package com.guardians.dto.mypage.res;

import com.guardians.domain.board.entity.Board;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ResPostDto {

    private List<PostInfo> posts;

    @Getter
    @Builder
    public static class PostInfo {
        private Long boardId;
        private String title;
        private String content;
        private String boardType;
    }

    public static ResPostDto fromEntities(List<Board> boardList) {
        return ResPostDto.builder()
                .posts(
                        boardList.stream()
                                .map(board -> PostInfo.builder()
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

