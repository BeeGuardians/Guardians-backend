// 질문 수정 응답

package com.guardians.dto.question.res;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResUpdateQuestionDto {
    private Long id;
    private String title;
}

