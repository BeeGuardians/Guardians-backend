// 질문 단건 조회 응답

package com.guardians.dto.question.res;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResQuestionDetailDto {
    private Long id;
    private String title;
    private String content;
    private String username;
    private Long wargameId;
    private String wargameTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
