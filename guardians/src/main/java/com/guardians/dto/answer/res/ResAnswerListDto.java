package com.guardians.dto.answer.res;

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
    private String username;
    private LocalDateTime createdAt;
}
