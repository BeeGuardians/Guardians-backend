package com.guardians.service.user.impl;

import com.guardians.config.AwsS3Properties;
import com.guardians.domain.user.entity.Role;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.repository.UserRepository;
import com.guardians.dto.user.res.ResCreateUserDto;
import com.guardians.dto.user.res.ResLoginDto;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import com.guardians.service.auth.EmailVerificationService;
import com.guardians.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final AwsS3Properties awsS3Properties;

    // 중복 검사
    private void validateDuplicate(String email, String username) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        if (userRepository.existsByUsername(username)) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
    }

    private User getUserWithStats(Long userId) {
        return userRepository.findWithStatsById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    @Override
    public ResCreateUserDto createUser(String username, String email, String password) {
        validateDuplicate(email, username);

        String encodedPw = passwordEncoder.encode(password);

        User user = User.create(
                username,
                email,
                encodedPw,
                Role.USER,
                awsS3Properties.getDefaultProfileUrl()
        );

        // 여기서 userStats도 내부에서 생성되어 연결된 상태
        User saved = userRepository.save(user); // 🚨 userStats도 cascade로 같이 저장됨

        return ResCreateUserDto.fromEntity(saved);
    }

    @Transactional
    @Override
    public ResLoginDto login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        user.updateLastLoginAt();

        return ResLoginDto.fromEntity(user);
    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.updateRole(newRole);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    @Override
    public ResLoginDto updateUserInfo(Long sessionUserId, Long targetUserId, String username) {
        if (!sessionUserId.equals(targetUserId)) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED); // ← 권한 없음 에러 따로 만들자
        }

        User user = getUserWithStats(targetUserId);
        user.updateUsername(username);

        return ResLoginDto.fromEntity(user);
    }

    @Transactional
    @Override
    public void changePassword(Long sessionUserId, Long targetUserId, String currentPassword, String newPassword) {
        if (!sessionUserId.equals(targetUserId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        User user = getUserWithStats(sessionUserId);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.updatePassword(encodedNewPassword);
    }

    @Transactional
    @Override
    public void verifyResetPassword(Long userId, String code, String newPassword) {
        User user = getUserWithStats(userId);

        boolean verified = emailVerificationService.verifyCode(user.getEmail(), code);
        if (!verified) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResLoginDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(ResLoginDto::fromEntity)
                .toList();
    }

    @Transactional
    @Override
    public void deleteUser(Long sessionUserId, Long targetUserId) {
        if (sessionUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS); // 세션 없음
        }

        if (!sessionUserId.equals(targetUserId)) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED); // 본인만 탈퇴 가능
        }

        userRepository.deleteById(targetUserId);
    }

    @Override
    public void adminDeleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }


    @Override
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

    @Override
    public String getEmailByUserId(Long userId) {
        return getUserWithStats(userId).getEmail();
    }


    @Override
    public Long findUserIdByEmail(String email) {
        return userRepository.findIdByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    @Override
    public void updateProfileImageUrl(Long userId, String imageUrl) {
        getUserWithStats(userId).updateProfileImageUrl(imageUrl);
    }

}
