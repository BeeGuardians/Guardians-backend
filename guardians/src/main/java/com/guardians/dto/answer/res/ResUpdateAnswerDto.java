// 답변 수정 응답

package com.guardians.dto.answer.res;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResUpdateAnswerDto {
    private Long id;
    private String content;
}