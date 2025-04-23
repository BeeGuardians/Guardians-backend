package com.guardians.dto.user.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReqLoginDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
