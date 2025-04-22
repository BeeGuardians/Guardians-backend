package com.guardians.controller;

import com.guardians.dto.common.ResWrapper;
import com.guardians.dto.user.req.ReqCreateUserDto;
import com.guardians.dto.user.req.ReqLoginDto;
import com.guardians.dto.user.res.ResCreateUserDto;
import com.guardians.dto.user.res.ResLoginDto;
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

    // 회원가입
    @Operation(summary = "회원가입", description = "유저 정보를 받아 회원가입 처리")
    @PostMapping
    public ResponseEntity<ResWrapper<?>> createUser(@RequestBody @Valid ReqCreateUserDto requestDto) {
        ResCreateUserDto createdUser = userService.createUser(requestDto);
        return ResponseEntity.ok(ResWrapper.resSuccess("회원가입 성공",createdUser));
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

    // 로그아웃
    @Operation(summary = "로그아웃", description = "세션을 만료시켜 로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ResWrapper<?>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ResWrapper.resSuccess("로그아웃 완료", null));
    }
}
