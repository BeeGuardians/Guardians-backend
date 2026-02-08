// 질문 수정 응답

package com.guardians.dto.question.res;

import com.guardians.domain.board.entity.Question;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResUpdateQuestionDto {
    private Long id;
    private String title;

    public static ResUpdateQuestionDto fromEntity(Question question) {
        return ResUpdateQuestionDto.builder()
                .id(question.getId())
                .title(question.getTitle())
                .build();
    }
}

