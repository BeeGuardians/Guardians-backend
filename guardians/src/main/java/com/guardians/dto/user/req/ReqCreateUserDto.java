package com.guardians.dto.user.req;

import com.guardians.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class ReqCreateUserDto {

    @Schema(description = "사용자 ID (3~20자)", example = "guardian123")
    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 1, max = 20)
    private String username;

    @Schema(description = "사용자 이메일", example = "user@example.com")
    @NotBlank
    @Email
    private String email;

    @Schema(description = "비밀번호 (6자 이상)", example = "secureP@ss1")
    @NotBlank
    @Size(min = 6)
    private String password;

    @Builder
    public ReqCreateUserDto(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Service에서 바로 entity 생성 가능
    public User toEntity(String encodedPassword, String defaultProfileImageUrl) {
        return User.create(username, email, encodedPassword, "USER", defaultProfileImageUrl);
    }
}
