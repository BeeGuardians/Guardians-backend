package com.guardians.controller;

import com.guardians.application.auth.AuthFacade;
import com.guardians.application.user.UserFacade;
import com.guardians.domain.user.entity.Role;
import com.guardians.dto.common.ResWrapper;
import com.guardians.dto.user.req.*;
import com.guardians.dto.user.res.ResCreateUserDto;
import com.guardians.dto.user.res.ResLoginDto;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import com.guardians.util.SessionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "회원가입 및 로그인 관련 API")
public class UserController {

    private final UserFacade userFacade;
    private final AuthFacade authFacade;

    @Operation(summary = "회원가입", description = "유저 정보를 받아 회원가입 처리")
    @PostMapping
    public ResponseEntity<ResWrapper<?>> createUser(@RequestBody @Valid ReqCreateUserDto requestDto) {
        ResCreateUserDto createdUser = userFacade.createUser(requestDto.getUsername(), requestDto.getEmail(), requestDto.getPassword());
        return ResponseEntity.ok(ResWrapper.resSuccess("회원가입 성공", createdUser));
    }

    @Operation(summary = "이메일 인증코드 전송", description = "입력한 이메일로 인증코드 발송")
    @GetMapping("/send-code")
    public ResponseEntity<ResWrapper<?>> sendSignupCode(@RequestParam("email") String email) {
        authFacade.sendVerificationCode(email, "mail/signup-verification.html");
        return ResponseEntity.ok(ResWrapper.resSuccess("회원가입 인증 메일 전송 완료", null));
    }

    @Operation(summary = "이메일 중복 확인", description = "이미 가입된 이메일인지 확인")
    @GetMapping("/check-email")
    public ResponseEntity<ResWrapper<?>> checkEmailExists(@RequestParam("email") String email) {
        boolean exists = userFacade.isEmailExists(email);
        return ResponseEntity.ok(ResWrapper.resSuccess("이메일 존재 여부", exists));
    }

    @Operation(summary = "이메일 인증코드 검증", description = "입력한 코드가 유효한지 확인")
    @PostMapping("/verify-code")
    public ResponseEntity<ResWrapper<?>> verifyCode(
            @RequestParam("email") String email,
            @RequestParam("code") String code
    ) {
        boolean isValid = authFacade.verifyCode(email, code);
        return ResponseEntity.ok(ResWrapper.resSuccess("인증 결과", isValid));
    }

    @Operation(summary = "로그인 여부 확인", description = "현재 세션에 유저 정보가 존재하는지 확인합니다.")
    @GetMapping("/check")
    public ResponseEntity<?> checkLogin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("userId") != null);
        return ResponseEntity.ok(ResWrapper.resSuccess("로그인 여부 확인", isLoggedIn));
    }

    @PostMapping("/login")
    public ResponseEntity<ResWrapper<?>> login(
            @RequestBody @Valid ReqLoginDto loginDto,
            HttpSession session
    ) {
        ResLoginDto loginUser = userFacade.login(loginDto.getEmail(), loginDto.getPassword());
        session.setAttribute("userId", loginUser.getId());
        session.setAttribute("role", loginUser.getRole());

        return ResponseEntity.ok(ResWrapper.resSuccess("로그인 성공", loginUser));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<ResWrapper<?>> adminLogin(
            @RequestBody @Valid ReqLoginDto loginDto,
            HttpSession session
    ) {
        ResLoginDto loginUser = userFacade.login(loginDto.getEmail(), loginDto.getPassword());

        if (Role.valueOf(loginUser.getRole()) != Role.ADMIN) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }

        session.setAttribute("userId", loginUser.getId());
        session.setAttribute("role", loginUser.getRole());

        return ResponseEntity.ok(ResWrapper.resSuccess("[관리자] 로그인 성공", loginUser));
    }

    @PutMapping("/admin/update-role/{userId}")
    public ResponseEntity<ResWrapper<?>> updateUserRole(
            @PathVariable Long userId,
            @RequestBody ReqUpdateUserRoleDto request,
            HttpSession session
    ) {
        SessionUtil.requireAdmin(session);

        Role role = Role.valueOf(request.getRole());
        userFacade.updateUserRole(userId, role);
        return ResponseEntity.ok(ResWrapper.resSuccess("[관리자] 권한 변경 완료", null));
    }

    @Operation(summary = "전체 유저 목록 조회", description = "관리자만 전체 유저 목록을 확인할 수 있습니다.")
    @GetMapping("/admin/list")
    public ResponseEntity<ResWrapper<?>> getAllUsers(HttpSession session) {
        SessionUtil.requireAdmin(session);

        List<ResLoginDto> users = userFacade.getAllUsers();
        return ResponseEntity.ok(ResWrapper.resList("[관리자] 전체 유저 목록 반환", users, users.size()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResWrapper<?>> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(ResWrapper.resSuccess("로그아웃 완료 (세션 + 쿠키 삭제)", null));
    }

    @PatchMapping("/{userId}/update")
    public ResponseEntity<ResWrapper<?>> updateUserInfo(
            @PathVariable("userId") Long userId,
            @RequestBody @Valid ReqUpdateUserDto updateDto,
            HttpSession session
    ) {
        Long sessionUserId = SessionUtil.getRequiredUserId(session);

        ResLoginDto updatedUser = userFacade.updateUserInfo(sessionUserId, userId, updateDto.getUsername());

        return ResponseEntity.ok(ResWrapper.resSuccess("회원 정보 수정 완료", updatedUser));
    }

    @Operation(summary = "프로필 이미지 업로드", description = "S3에 프로필 이미지를 업로드하고 URL을 저장")
    @PostMapping("/{userId}/profile-image")
    public ResponseEntity<ResWrapper<?>> uploadProfileImage(
            @PathVariable("userId") Long userId,
            @RequestParam("file") MultipartFile file,
            HttpSession session
    ) throws IOException {
        SessionUtil.requireSameUser(session, userId);
        String imageUrl = userFacade.uploadProfileImage(userId, file);
        return ResponseEntity.ok(ResWrapper.resSuccess("프로필 이미지 업로드 성공", imageUrl));
    }

    @Operation(summary = "프로필 이미지 삭제", description = "프로필 이미지를 기본 이미지로 롤백")
    @DeleteMapping("/{userId}/profile-image")
    public ResponseEntity<ResWrapper<?>> deleteProfileImage(
            @PathVariable("userId") Long userId,
            HttpSession session
    ) {
        SessionUtil.requireSameUser(session, userId);
        String defaultUrl = userFacade.resetProfileImage(userId);
        return ResponseEntity.ok(ResWrapper.resSuccess("프로필 이미지 기본으로 변경 완료", defaultUrl));
    }

    @PatchMapping("/{userId}/reset-password")
    public ResponseEntity<ResWrapper<?>> changePassword(
            @PathVariable("userId") Long userId,
            @RequestBody @Valid ReqChangePasswordDto dto,
            HttpSession session
    ) {
        Long sessionUserId = SessionUtil.getRequiredUserId(session);

        userFacade.changePassword(sessionUserId, userId, dto.getCurrentPassword(), dto.getNewPassword());

        return ResponseEntity.ok(ResWrapper.resSuccess("비밀번호 변경 완료", null));
    }

    @Operation(summary = "비밀번호 찾기 - 이메일 인증 코드 전송", description = "비밀번호 재설정을 위한 이메일 인증 코드 발송")
    @GetMapping("/{userId}/reset-password/send-code")
    public ResponseEntity<ResWrapper<?>> sendResetPasswordCode(@PathVariable Long userId) {
        String email = userFacade.getEmailByUserId(userId);
        authFacade.sendVerificationCode(email, "mail/password-reset.html");
        return ResponseEntity.ok(ResWrapper.resSuccess("비밀번호 재설정 코드 발송 완료", null));
    }

    @Operation(summary = "비밀번호 찾기 - 비밀번호 재설정", description = "인증 코드 검증 후 새로운 비밀번호 설정")
    @PostMapping("/{userId}/reset-password/verify-code")
    public ResponseEntity<ResWrapper<?>> verifyResetPassword(
            @PathVariable("userId") Long userId,
            @RequestParam String code,
            @RequestParam String newPassword
    ) {
        userFacade.verifyResetPassword(userId, code, newPassword);
        return ResponseEntity.ok(ResWrapper.resSuccess("비밀번호 재설정 완료", null));
    }

    @Operation(summary = "이메일로 유저 ID 조회", description = "입력된 이메일로 등록된 유저 ID를 반환")
    @GetMapping("/find-id")
    public ResponseEntity<ResWrapper<?>> findUserIdByEmail(@RequestParam String email) {
        Long userId = userFacade.findUserIdByEmail(email);
        return ResponseEntity.ok(ResWrapper.resSuccess("유저 ID 반환", userId));
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 처리")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ResWrapper<?>> deleteUser(
            @PathVariable Long userId,
            HttpSession session
    ) {
        Long sessionUserId = SessionUtil.getRequiredUserId(session);

        userFacade.deleteUser(sessionUserId, userId);
        session.invalidate();

        return ResponseEntity.ok(ResWrapper.resSuccess("회원 탈퇴 완료", null));
    }

    @Operation(summary = "관리자 전용 회원 삭제", description = "관리자가 특정 회원을 강제 탈퇴시킵니다.")
    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<ResWrapper<?>> adminDeleteUser(
            @PathVariable Long userId,
            HttpSession session
    ) {
        SessionUtil.requireAdmin(session);

        userFacade.adminDeleteUser(userId);
        return ResponseEntity.ok(ResWrapper.resSuccess("[관리자] 회원 삭제 완료", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ResWrapper<?>> getCurrentUser(HttpSession session) {
        Long userId = SessionUtil.getRequiredUserId(session);
        ResLoginDto user = userFacade.getUserInfo(userId);
        return ResponseEntity.ok(ResWrapper.resSuccess("유저 정보 조회 성공", user));
    }
}
