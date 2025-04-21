package com.guardians.dto.user.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResCreateUserDto {

    private Long id;
    private String username;
    private String email;
}
