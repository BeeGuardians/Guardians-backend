package com.guardians.controller;

import com.guardians.dto.common.ResWrapper;
import com.guardians.dto.user.req.ReqCreateUserDto;
import com.guardians.dto.user.req.ReqLoginDto;
import com.guardians.dto.user.req.ReqUpdateUserDto;
import com.guardians.dto.user.res.ResCreateUserDto;
import com.guardians.dto.user.res.ResLoginDto;
import com.guardians.service.auth.EmailVerificationService;
import com.guardians.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    // 로그아웃
    @Operation(summary = "로그아웃", description = "세션을 만료시켜 로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ResWrapper<?>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ResWrapper.resSuccess("로그아웃 완료", null));
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

    // 비밀번호 찾기

    // 회원 탈퇴

}
