// 답변 등록 요청

package com.guardians.dto.answer.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateAnswerDto {
    private Long questionId;
    private String content;
}
