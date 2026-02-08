// 질문 단건 조회 응답

package com.guardians.dto.question.res;

import com.guardians.domain.board.entity.Question;
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
    private String userId;
    private Long wargameId;
    private String wargameTitle;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int viewCount;

    public static ResQuestionDetailDto fromEntity(Question question) {
        return ResQuestionDetailDto.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .username(question.getUser().getUsername())
                .userId(String.valueOf(question.getUser().getId()))
                .wargameId(question.getWargame().getId())
                .wargameTitle(question.getWargame().getTitle())
                .profileImageUrl(question.getUser().getProfileImageUrl())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .viewCount(question.getViewCount())
                .build();
    }
}
