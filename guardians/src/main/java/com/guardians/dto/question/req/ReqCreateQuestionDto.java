// 질문 등록 요청

package com.guardians.dto.question.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateQuestionDto {
    private String title;
    private String content;
    private Long wargameId;
}
