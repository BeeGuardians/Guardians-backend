package com.guardians.dto.board.res;

import com.guardians.domain.board.entity.Comment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResCreateCommentDto {
    private Long commentId;
    private String content;
    private String username;

    public static ResCreateCommentDto fromEntity(Comment comment) {
        return ResCreateCommentDto.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .username(comment.getUser().getUsername())
                .build();
    }
}
