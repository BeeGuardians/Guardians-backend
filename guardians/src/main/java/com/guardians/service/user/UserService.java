package com.guardians.service.user;

import com.guardians.dto.user.req.ReqChangePasswordDto;
import com.guardians.dto.user.req.ReqCreateUserDto;
import com.guardians.dto.user.req.ReqLoginDto;
import com.guardians.dto.user.req.ReqUpdateUserDto;
import com.guardians.dto.user.res.ResCreateUserDto;
import com.guardians.dto.user.res.ResLoginDto;

public interface UserService {
    ResCreateUserDto createUser(ReqCreateUserDto dto);
    ResLoginDto login(ReqLoginDto dto);
    ResLoginDto updateUserInfo(Long sessionUserId, Long targetUserId, ReqUpdateUserDto dto);
    void changePassword(Long sessionUserId, Long targetUserId, ReqChangePasswordDto dto);
    void verifyResetPassword(Long userId, String code, String newPassword);
    void deleteUser(Long sessionUserId, Long targetUserId);
    Long findUserIdByEmail(String email);
    String getEmailByUserId(Long userId);
    ResLoginDto getUserInfo(Long userId);
    boolean isEmailExists(String email);
    void updateProfileImageUrl(Long userId, String imageUrl);

}
