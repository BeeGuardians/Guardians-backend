package com.guardians.dto.mypage.res;

import com.guardians.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResProfileDto {

    private Long userId;
    private String username;
    private String email;
    private String profileImageUrl; // 프로필 이미지 있으면

    public static ResProfileDto fromEntity(User user) {
        return ResProfileDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl()) // 없으면 빼도 됨
                .build();
    }
}
