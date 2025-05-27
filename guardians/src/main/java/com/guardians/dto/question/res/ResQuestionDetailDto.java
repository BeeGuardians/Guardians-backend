// 질문 단건 조회 응답

package com.guardians.dto.question.res;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResQuestionDetailDto {
    private Long id;
    private String title;
    private String content;
    private String username;
    private String userId;
    private Long wargameId;
    private String wargameTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int viewCount;
}
