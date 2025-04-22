package com.guardians.dto.user.res;

import com.guardians.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResCreateUserDto {

    private Long id;
    private String username;
    private String email;

    public static ResCreateUserDto fromEntity(User user) {
        return ResCreateUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
