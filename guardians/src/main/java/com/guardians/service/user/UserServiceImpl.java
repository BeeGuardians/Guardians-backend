package com.guardians.service.user;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.repository.UserRepository;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

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

        User user = User.create(dto.getUsername(), dto.getEmail(), encodedPw, "USER");

        User saved = userRepository.save(user);
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

    @Transactional
    @Override
    public ResLoginDto updateUserInfo(Long sessionUserId, Long targetUserId, ReqUpdateUserDto dto) {
        if (!sessionUserId.equals(targetUserId)) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED); // ← 권한 없음 에러 따로 만들자
        }

        User user = userRepository.findById(targetUserId)
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

        User user = userRepository.findById(sessionUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String encodedNewPassword = passwordEncoder.encode(dto.getNewPassword());
        user.updatePassword(encodedNewPassword);
    }

    @Transactional
    @Override
    public void sendResetPasswordCode(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        emailVerificationService.sendVerificationCode(user.getEmail());
    }

    @Transactional
    @Override
    public void verifyResetPassword(Long userId, String code, String newPassword) {
        User user = userRepository.findById(userId)
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return ResLoginDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }


}
