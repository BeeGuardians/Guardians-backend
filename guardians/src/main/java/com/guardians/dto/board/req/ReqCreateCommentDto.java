package com.guardians.dto.board.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateCommentDto {
    @NotBlank(message = "댓글 내용을 입력해주세요.")
    @Size(min = 1, max = 1000, message = "댓글은 1자 이상 1000자 이하로 입력해주세요.")
    private String content;

    // username, createdAt, updatedAt, userId는 서버에서 설정하므로 클라이언트에서 전달받지 않음
}
