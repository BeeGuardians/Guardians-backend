// 질문 등록 요청

package com.guardians.dto.question.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateQuestionDto {
    @NotBlank(message = "제목을 입력해주세요.")
    @Size(min = 2, max = 100, message = "제목은 2자 이상 100자 이하로 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    @Size(min = 10, max = 5000, message = "내용은 10자 이상 5000자 이하로 입력해주세요.")
    private String content;

    @NotNull(message = "워게임 ID를 입력해주세요.")
    private Long wargameId;
}
