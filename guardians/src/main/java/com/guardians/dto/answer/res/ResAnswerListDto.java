package com.guardians.dto.answer.res;

import com.guardians.domain.board.entity.Answer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResAnswerListDto {
    private Long id;
    private String content;
    private Long userId;
    private String username;
    private String profileImageUrl;
    private String tier;
    private LocalDateTime createdAt;

    public static ResAnswerListDto fromEntity(Answer answer) {
        return ResAnswerListDto.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .userId(answer.getUser().getId())
                .username(answer.getUser().getUsername())
                .profileImageUrl(answer.getUser().getProfileImageUrl())
                .tier(answer.getUser().getUserStats().getTier().name())
                .createdAt(answer.getCreatedAt())
                .build();
    }
}
