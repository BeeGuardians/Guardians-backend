package com.guardians.application.user;

import com.guardians.domain.user.entity.Role;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.port.EmailVerificationPort;
import com.guardians.domain.user.port.UserPort;
import com.guardians.domain.user.service.UserDomainService;
import com.guardians.dto.user.res.ResCreateUserDto;
import com.guardians.dto.user.res.ResLoginDto;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import com.guardians.infrastructure.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFacade {

    private final UserPort userPort;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationPort emailVerificationPort;
    private final S3Service s3Service;
    private final UserDomainService userDomainService;

    private User getUserWithStats(Long userId) {
        return userPort.findWithStatsById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public ResCreateUserDto createUser(String username, String email, String password) {
        userDomainService.validateDuplicate(email, username);

        String encodedPw = passwordEncoder.encode(password);

        User user = User.create(
                username,
                email,
                encodedPw,
                Role.USER,
                s3Service.getDefaultProfileUrl()
        );

        User saved = userPort.save(user);
        return ResCreateUserDto.fromEntity(saved);
    }

    @Transactional
    public ResLoginDto login(String email, String password) {
        User user = userPort.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        user.updateLastLoginAt();
        return ResLoginDto.fromEntity(user);
    }

    @Transactional
    public void updateUserRole(Long userId, Role newRole) {
        User user = userPort.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.updateRole(newRole);
    }

    public boolean isEmailExists(String email) {
        return userPort.existsByEmail(email);
    }

    @Transactional
    public ResLoginDto updateUserInfo(Long sessionUserId, Long targetUserId, String username) {
        if (!sessionUserId.equals(targetUserId)) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }

        User user = getUserWithStats(targetUserId);
        user.updateUsername(username);
        return ResLoginDto.fromEntity(user);
    }

    @Transactional
    public void changePassword(Long sessionUserId, Long targetUserId, String currentPassword, String newPassword) {
        if (!sessionUserId.equals(targetUserId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        User user = getUserWithStats(sessionUserId);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void verifyResetPassword(Long userId, String code, String newPassword) {
        User user = getUserWithStats(userId);

        boolean verified = emailVerificationPort.verifyCode(user.getEmail(), code);
        if (!verified) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    public List<ResLoginDto> getAllUsers() {
        return userPort.findAll().stream()
                .map(ResLoginDto::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteUser(Long sessionUserId, Long targetUserId) {
        if (sessionUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (!sessionUserId.equals(targetUserId)) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }

        userPort.deleteById(targetUserId);
    }

    @Transactional
    public void adminDeleteUser(Long userId) {
        User user = userPort.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        userPort.delete(user);
    }

    public ResLoginDto getUserInfo(Long userId) {
        User user = getUserWithStats(userId);
        return ResLoginDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .lastLoginAt(user.getLastLoginAt())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    public String getEmailByUserId(Long userId) {
        return getUserWithStats(userId).getEmail();
    }

    public Long findUserIdByEmail(String email) {
        return userPort.findIdByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void updateProfileImageUrl(Long userId, String imageUrl) {
        getUserWithStats(userId).updateProfileImageUrl(imageUrl);
    }

    @Transactional
    public String uploadProfileImage(Long userId, MultipartFile file) throws IOException {
        String imageUrl = s3Service.uploadProfileImage(file);
        getUserWithStats(userId).updateProfileImageUrl(imageUrl);
        return imageUrl;
    }

    @Transactional
    public String resetProfileImage(Long userId) {
        String defaultUrl = s3Service.getDefaultProfileUrl();
        getUserWithStats(userId).updateProfileImageUrl(defaultUrl);
        return defaultUrl;
    }
}
