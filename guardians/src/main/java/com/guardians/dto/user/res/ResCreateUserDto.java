package com.guardians.dto.user.res;

import com.guardians.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "회원 생성 응답 DTO")
public class ResCreateUserDto {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 이름", example = "guardiansUser")
    private String username;

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    public static ResCreateUserDto fromEntity(User user) {
        return ResCreateUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}

