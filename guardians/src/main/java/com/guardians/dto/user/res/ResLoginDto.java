package com.guardians.dto.user.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.guardians.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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

    private String profileImageUrl;

    @Schema(description = "마지막 로그인 일시", example = "20250423220100")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmmss")
    private LocalDateTime lastLoginAt;

    @Schema(description = "사용자 권한", example = "USER")
    private String role;

    public static ResLoginDto fromEntity(User user) {
        return ResLoginDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .lastLoginAt(user.getLastLoginAt())
                .role(user.getRole())
                .build();
    }
}
