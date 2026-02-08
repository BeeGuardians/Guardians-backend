package com.guardians.dto.board.res;

import com.guardians.domain.board.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResUpdateCommentDto {
    private Long commentId;
    private String content;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;

    public static ResUpdateCommentDto fromEntity(Comment comment) {
        return ResUpdateCommentDto.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .username(comment.getUser().getUsername())
                .createdAt(comment.getCreatedAt())
                .userId(comment.getUser().getId())
                .build();
    }
}
