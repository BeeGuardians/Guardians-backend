// 답변 수정 응답

package com.guardians.dto.answer.res;

import com.guardians.domain.board.entity.Answer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResUpdateAnswerDto {
    private Long id;
    private String content;
    private Long userId;

    public static ResUpdateAnswerDto fromEntity(Answer answer) {
        return ResUpdateAnswerDto.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .userId(answer.getUser().getId())
                .build();
    }
}