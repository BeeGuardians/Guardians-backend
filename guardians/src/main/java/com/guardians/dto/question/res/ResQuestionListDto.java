// 질문 리스트 조회 응답

package com.guardians.dto.question.res;

import com.guardians.domain.board.entity.Question;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResQuestionListDto {
    private Long id;
    private String title;
    private String content;
    private String username;
    private Long wargameId;
    private String wargameTitle;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private int viewCount;

    public static ResQuestionListDto fromEntity(Question question) {
        return ResQuestionListDto.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .username(question.getUser().getUsername())
                .wargameId(question.getWargame().getId())
                .wargameTitle(question.getWargame().getTitle())
                .profileImageUrl(question.getUser().getProfileImageUrl())
                .createdAt(question.getCreatedAt())
                .viewCount(question.getViewCount())
                .build();
    }
}