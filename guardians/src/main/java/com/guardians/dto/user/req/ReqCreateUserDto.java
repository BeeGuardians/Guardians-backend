package com.guardians.dto.user.req;

import com.guardians.domain.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqCreateUserDto {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    public User toEntity() {
        return User.create(username, email, password); // create 메서드에서 해싱 처리할 수 있음
    }

}
