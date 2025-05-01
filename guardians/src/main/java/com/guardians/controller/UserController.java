package com.guardians.controller;

import com.guardians.dto.common.ResWrapper;
import com.guardians.dto.user.req.ReqChangePasswordDto;
import com.guardians.dto.user.req.ReqCreateUserDto;
import com.guardians.dto.user.req.ReqLoginDto;
import com.guardians.dto.user.req.ReqUpdateUserDto;
import com.guardians.dto.user.res.ResCreateUserDto;
import com.guardians.dto.user.res.ResLoginDto;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import com.guardians.service.auth.EmailVerificationService;
import com.guardians.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "회원가입 및 로그인 관련 API")
public class UserController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    // 회원가입
    @Operation(summary = "회원가입", description = "유저 정보를 받아 회원가입 처리")
    @PostMapping
    public ResponseEntity<ResWrapper<?>> createUser(@RequestBody @Valid ReqCreateUserDto requestDto) {
        ResCreateUserDto createdUser = userService.createUser(requestDto);
        return ResponseEntity.ok(ResWrapper.resSuccess("회원가입 성공",createdUser));
    }

    // 이메일 인증 코드 전송
    @Operation(summary = "이메일 인증코드 전송", description = "입력한 이메일로 인증코드 발송")
    @GetMapping("/send-code")
    public ResponseEntity<ResWrapper<?>> sendCode(@RequestParam String email) {
        emailVerificationService.sendVerificationCode(email);
        return ResponseEntity.ok(ResWrapper.resSuccess("인증 코드 전송 완료", null));
    }

    // 이메일 인증 코드 확인
    @Operation(summary = "이메일 인증코드 검증", description = "입력한 코드가 유효한지 확인")
    @PostMapping("/verify-code")
    public ResponseEntity<ResWrapper<?>> verifyCode(
            @RequestParam String email,
            @RequestParam String code
    ) {
        boolean isValid = emailVerificationService.verifyCode(email, code);
        return ResponseEntity.ok(ResWrapper.resSuccess("인증 결과", isValid));
    }

    // 로그인 여부 확인
    @Operation(summary = "로그인 여부 확인", description = "현재 세션에 유저 정보가 존재하는지 확인합니다.")
    @GetMapping("/check")
    public ResponseEntity<?> checkLogin(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 🔥 세션 강제 생성 방지
        boolean isLoggedIn = (session != null && session.getAttribute("userId") != null);
        return ResponseEntity.ok(ResWrapper.resSuccess("로그인 여부 확인", isLoggedIn));
    }

    // 로그인
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인")
    @PostMapping("/login")
    public ResponseEntity<ResWrapper<?>> login(
            @RequestBody @Valid ReqLoginDto loginDto,
            HttpSession session
    ) {
        ResLoginDto loginUser = userService.login(loginDto);

        // Redis 세션에 사용자 정보 저장
        session.setAttribute("userId", loginUser.getId());

        return ResponseEntity.ok(ResWrapper.resSuccess("로그인 성공", loginUser));
    }

    @Operation(summary = "로그아웃", description = "세션을 만료시키고 JSESSIONID 쿠키를 제거합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ResWrapper<?>> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        System.out.println("🔥 invalidate 전 세션 ID: " + session.getId());

        session.invalidate();
        System.out.println("🔥 invalidate 후 세션 ID: " + session.getId());

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);        // 만료
        cookie.setPath("/");        // 경로 맞춰야 삭제됨
        cookie.setHttpOnly(true);   // 클라이언트 JS 접근 방지 (선택)
        response.addCookie(cookie);

        return ResponseEntity.ok(ResWrapper.resSuccess("로그아웃 완료 (세션 + 쿠키 삭제)", null));
    }


    // 유저정보 - 닉네임 수정
    @PatchMapping("/{userId}/update")
    public ResponseEntity<ResWrapper<?>> updateUserInfo(
            @PathVariable Long userId,
            @RequestBody @Valid ReqUpdateUserDto updateDto,
            HttpSession session
    ) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        ResLoginDto updatedUser = userService.updateUserInfo(sessionUserId, userId, updateDto);

        return ResponseEntity.ok(ResWrapper.resSuccess("회원 정보 수정 완료", updatedUser));
    }

    // 프로필 사진 업로드

    // 비밀번호 변경
    @PatchMapping("/{userId}/reset-password")
    public ResponseEntity<ResWrapper<?>> changePassword(
            @PathVariable Long userId,
            @RequestBody @Valid ReqChangePasswordDto dto,
            HttpSession session
    ) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        userService.changePassword(sessionUserId, userId, dto);

        return ResponseEntity.ok(ResWrapper.resSuccess("비밀번호 변경 완료", null));
    }

    // 비밀번호 찾기 - 이메일 인증 코드 전송
    @Operation(summary = "비밀번호 찾기 - 이메일 인증 코드 전송", description = "비밀번호 재설정을 위한 이메일 인증 코드 발송")
    @GetMapping("/{userId}/reset-password/send-code")
    public ResponseEntity<ResWrapper<?>> sendResetPasswordCode(
            @PathVariable Long userId
    ) {
        userService.sendResetPasswordCode(userId);
        return ResponseEntity.ok(ResWrapper.resSuccess("비밀번호 재설정 코드 발송 완료", null));
    }


    // 비밀번호 찾기 - 이메일 검증 / 재설정
    @Operation(summary = "비밀번호 찾기 - 비밀번호 재설정", description = "인증 코드 검증 후 새로운 비밀번호 설정")
    @PostMapping("/{userId}/reset-password/verify-code")
    public ResponseEntity<ResWrapper<?>> verifyResetPassword(
            @PathVariable Long userId,
            @RequestParam String code,
            @RequestParam String newPassword
    ) {
        userService.verifyResetPassword(userId, code, newPassword);
        return ResponseEntity.ok(ResWrapper.resSuccess("비밀번호 재설정 완료", null));
    }

    // 회원 탈퇴
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 처리")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ResWrapper<?>> deleteUser(
            @PathVariable Long userId,
            HttpSession session
    ) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        if (sessionUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS); // 세션 없음
        }

        userService.deleteUser(sessionUserId, userId);
        session.invalidate(); // 탈퇴했으면 세션도 깨줘야지

        return ResponseEntity.ok(ResWrapper.resSuccess("회원 탈퇴 완료", null));
    }

    // UserId 반환
    @GetMapping("/me")
    public ResponseEntity<ResWrapper<?>> getCurrentUser(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
            }
            ResLoginDto user = userService.getUserInfo(userId);
            return ResponseEntity.ok(ResWrapper.resSuccess("유저 정보", user));
        } catch (Exception e) {
            return ResponseEntity.ok(ResWrapper.resException(e)); // 여기서 null 넘기면 위처럼 터짐
        }
    }

}
