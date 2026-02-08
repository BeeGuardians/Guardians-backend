// 답변 등록 응답

package com.guardians.dto.answer.res;

import com.guardians.domain.board.entity.Answer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResCreateAnswerDto {
    private Long id;
    private String content;

    public static ResCreateAnswerDto fromEntity(Answer answer) {
        return ResCreateAnswerDto.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .build();
    }
}
