package com.guardians.service.user;

import com.guardians.config.AwsS3Properties;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.entity.UserStats;
import com.guardians.domain.user.repository.UserRepository;
import com.guardians.domain.user.repository.UserStatsRepository;
import com.guardians.dto.user.req.ReqChangePasswordDto;
import com.guardians.dto.user.req.ReqCreateUserDto;
import com.guardians.dto.user.req.ReqLoginDto;
import com.guardians.dto.user.req.ReqUpdateUserDto;
import com.guardians.dto.user.res.ResCreateUserDto;
import com.guardians.dto.user.res.ResLoginDto;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import com.guardians.service.auth.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final AwsS3Properties awsS3Properties;
    private final UserStatsRepository userStatsRepository;

    // 중복 검사
    private void validateDuplicate(ReqCreateUserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
    }

    @Transactional
    @Override
    public ResCreateUserDto createUser(ReqCreateUserDto dto) {
        validateDuplicate(dto);

        String encodedPw = passwordEncoder.encode(dto.getPassword());

        User user = User.create(
                dto.getUsername(),
                dto.getEmail(),
                encodedPw,
                "USER",
                awsS3Properties.getDefaultProfileUrl()
        );

        // 여기서 userStats도 내부에서 생성되어 연결된 상태
        User saved = userRepository.save(user); // 🚨 userStats도 cascade로 같이 저장됨

        return ResCreateUserDto.fromEntity(saved);
    }

    @Transactional
    @Override
    public ResLoginDto login(ReqLoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        user.updateLastLoginAt();

        return ResLoginDto.fromEntity(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    @Override
    public ResLoginDto updateUserInfo(Long sessionUserId, Long targetUserId, ReqUpdateUserDto dto) {
        if (!sessionUserId.equals(targetUserId)) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED); // ← 권한 없음 에러 따로 만들자
        }

        User user = userRepository.findWithStatsById(targetUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.updateUsername(dto.getUsername());

        return ResLoginDto.fromEntity(user);
    }

    @Transactional
    @Override
    public void changePassword(Long sessionUserId, Long targetUserId, ReqChangePasswordDto dto) {
        if (!sessionUserId.equals(targetUserId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        User user = userRepository.findWithStatsById(sessionUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String encodedNewPassword = passwordEncoder.encode(dto.getNewPassword());
        user.updatePassword(encodedNewPassword);
    }

    @Transactional
    @Override
    public void verifyResetPassword(Long userId, String code, String newPassword) {
        User user = userRepository.findWithStatsById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        boolean verified = emailVerificationService.verifyCode(user.getEmail(), code);
        if (!verified) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
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
    public ResLoginDto getUserInfo(Long userId) {
        User user = userRepository.findWithStatsById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return ResLoginDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .lastLoginAt(user.getLastLoginAt())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    @Override
    public String getEmailByUserId(Long userId) {
        User user = userRepository.findWithStatsById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return user.getEmail();
    }


    @Override
    public Long findUserIdByEmail(String email) {
        return userRepository.findIdByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    @Override
    public void updateProfileImageUrl(Long userId, String imageUrl) {
        User user = userRepository.findWithStatsById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.updateProfileImageUrl(imageUrl);
        // 변경 감지를 위해 save 호출 불필요 (JPA 엔티티 상태 유지 중)
    }

}
