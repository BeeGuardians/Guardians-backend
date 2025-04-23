package com.guardians.dto.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "로그인 요청 DTO")
public class ReqLoginDto {

    @Schema(description = "이메일 주소", example = "user@example.com")
    @NotBlank
    @Email
    private String email;

    @Schema(description = "비밀번호", example = "secureP@ss1!")
    @NotBlank
    private String password;
}

