package com.guardians.dto.board.res;

import com.guardians.domain.board.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResCommentListDto {
    private Long commentId;
    private String content;
    private String username;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;

    public static ResCommentListDto fromEntity(Comment comment) {
        return ResCommentListDto.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .username(comment.getUser().getUsername())
                .profileImageUrl(comment.getUser().getProfileImageUrl())
                .createdAt(comment.getCreatedAt())
                .userId(comment.getUser().getId())
                .build();
    }
}