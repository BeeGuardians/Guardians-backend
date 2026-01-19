// 답변 수정 요청

package com.guardians.dto.answer.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateAnswerDto {
    @NotBlank(message = "답변 내용을 입력해주세요.")
    @Size(min = 5, max = 5000, message = "답변 내용은 5자 이상 5000자 이하로 입력해주세요.")
    private String content;
}
