package com.guardians.service.user;

import com.guardians.domain.user.entity.Role;
import com.guardians.dto.user.req.ReqChangePasswordDto;
import com.guardians.dto.user.req.ReqCreateUserDto;
import com.guardians.dto.user.req.ReqLoginDto;
import com.guardians.dto.user.req.ReqUpdateUserDto;
import com.guardians.dto.user.res.ResCreateUserDto;
import com.guardians.dto.user.res.ResLoginDto;

import java.util.List;

public interface UserService {
    ResCreateUserDto createUser(ReqCreateUserDto dto);
    ResLoginDto login(ReqLoginDto dto);
    ResLoginDto updateUserInfo(Long sessionUserId, Long targetUserId, ReqUpdateUserDto dto);
    void changePassword(Long sessionUserId, Long targetUserId, ReqChangePasswordDto dto);
    void verifyResetPassword(Long userId, String code, String newPassword);
    void deleteUser(Long sessionUserId, Long targetUserId);
    void adminDeleteUser(Long userId);
    void updateUserRole(Long userId, Role newRole);
    void updateProfileImageUrl(Long userId, String imageUrl);

    // READ는 그대로 서비스계층에
    List<ResLoginDto> getAllUsers();
    ResLoginDto getUserInfo(Long userId);
    Long findUserIdByEmail(String email);
    String getEmailByUserId(Long userId);
    boolean isEmailExists(String email);

}
