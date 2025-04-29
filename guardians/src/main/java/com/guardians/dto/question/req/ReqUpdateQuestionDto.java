// 질문 수정 요청

package com.guardians.dto.question.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateQuestionDto {
    private String title;
    private String content;
}
