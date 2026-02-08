// 질문 등록 응답

package com.guardians.dto.question.res;

import com.guardians.domain.board.entity.Question;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResCreateQuestionDto {
    private Long id;
    private String title;

    public static ResCreateQuestionDto fromEntity(Question question) {
        return ResCreateQuestionDto.builder()
                .id(question.getId())
                .title(question.getTitle())
                .build();
    }
}