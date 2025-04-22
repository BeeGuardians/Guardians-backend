package com.guardians.dto.user.res;

import com.guardians.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResLoginDto {
    private Long id;
    private String username;
    private String email;

    public static ResLoginDto fromEntity(User user) {
        return ResLoginDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
