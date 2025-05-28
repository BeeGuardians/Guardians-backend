// 답변 단건 조회 응답

package com.guardians.dto.answer.res;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResAnswerDetailDto {
    private Long id;
    private String content;
    private String username;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
