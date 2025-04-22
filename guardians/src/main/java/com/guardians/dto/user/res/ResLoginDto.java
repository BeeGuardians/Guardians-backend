package com.guardians.dto.user.res;

import com.guardians.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "로그인 응답 DTO")
public class ResLoginDto {

    @Schema(description = "유저 ID", example = "1")
    private Long id;

    @Schema(description = "유저 이름", example = "jhjh12")
    private String username;

    @Schema(description = "유저 이메일", example = "hacker01@example.com")
    private String email;

    public static ResLoginDto fromEntity(User user) {
        return ResLoginDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}

