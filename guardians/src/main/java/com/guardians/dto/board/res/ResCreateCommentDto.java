package com.guardians.dto.board.res;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResCreateCommentDto {
    private Long commentId;
    private String content;
    private String username;
}
