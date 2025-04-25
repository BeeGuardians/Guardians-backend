package com.guardians.dto.user.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateUserDto {

    @NotBlank(message = "닉네임은 필수 입력입니다.")
    private String username;
}
