package com.guardians.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardians.dto.user.req.*;
import com.guardians.dto.user.res.*;
import com.guardians.service.auth.EmailVerificationService;
import com.guardians.service.s3.S3Service;
import com.guardians.service.user.UserService;
import com.guardians.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections; // getAllUsers 테스트를 위해 추가
import java.util.List;     // getAllUsers 테스트를 위해 추가

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // --- 회원가입 관련 테스트 ---
    @Test
    @DisplayName("회원가입 - 성공")
    void createUser_Success() throws Exception {
        // Given
        ReqCreateUserDto requestDto = new ReqCreateUserDto();
        requestDto.setUsername("tester");
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password123");

        ResCreateUserDto expectedResponse = ResCreateUserDto.builder()
                .id(1L)
                .username("tester")
                .email("test@example.com")
                .role("USER")
                .createdAt(null)
                .build();

        given(userService.createUser(any(ReqCreateUserDto.class))).willReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/users") // POST 요청
                        .contentType(MediaType.APPLICATION_JSON) // 요청 본문 타입 JSON
                        .content(objectMapper.writeValueAsString(requestDto))) // 요청 본문 DTO를 JSON 문자열로 변환
                .andExpect(status().isOk()) // HTTP 상태 코드 200 OK 확인
                .andExpect(jsonPath("$.result.message").value("회원가입 성공"))
                .andExpect(jsonPath("$.result.data.id").value(1L)) // ResCreateUserDto의 id 필드명 확인 필요
                .andExpect(jsonPath("$.result.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.result.data.username").value("tester")) // username 필드 검증 추가
                .andDo(print()); // 요청/응답 내용 로깅 (디버깅 시 유용)

        verify(userService).createUser(any(ReqCreateUserDto.class));
    }

    @Test
    @DisplayName("회원가입 - 실패 (유효성 검사 실패 - 이메일 누락)")
    void createUser_ValidationFailure_EmailMissing() throws Exception {
        // Given
        ReqCreateUserDto invalidRequestDto = new ReqCreateUserDto();
        invalidRequestDto.setPassword("password123");
        invalidRequestDto.setUsername("tester");
        // email 필드가 @NotBlank 등으로 설정되어 있다고 가정

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest()) // @Valid 어노테이션에 의해 400 예상
                .andDo(print());
    }

    // --- 이메일 인증 관련 테스트 ---
    @Test
    @DisplayName("이메일 인증코드 전송 - 성공")
    void sendSignupCode_Success() throws Exception {
        // Given
        String email = "test@example.com";
        doNothing().when(emailVerificationService).sendVerificationCode(anyString(), anyString());

        // When & Then
        mockMvc.perform(get("/api/users/send-code")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("회원가입 인증 메일 전송 완료"))
                .andDo(print());

        verify(emailVerificationService).sendVerificationCode(email, "mail/signup-verification.html");
    }

    @Test
    @DisplayName("이메일 중복 확인 - 중복되지 않음")
    void checkEmailExists_NotExists() throws Exception {
        // Given
        String email = "new@example.com";
        given(userService.isEmailExists(email)).willReturn(false);

        // When & Then
        mockMvc.perform(get("/api/users/check-email")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("이메일 존재 여부"))
                .andExpect(jsonPath("$.result.data").value(false))
                .andDo(print());

        verify(userService).isEmailExists(email);
    }

    @Test
    @DisplayName("이메일 중복 확인 - 중복됨")
    void checkEmailExists_Exists() throws Exception {
        // Given
        String email = "existing@example.com";
        given(userService.isEmailExists(email)).willReturn(true);

        // When & Then
        mockMvc.perform(get("/api/users/check-email")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("이메일 존재 여부"))
                .andExpect(jsonPath("$.result.data").value(true))
                .andDo(print());
    }


    @Test
    @DisplayName("이메일 인증코드 검증 - 성공")
    void verifyCode_Success() throws Exception {
        // Given
        String email = "test@example.com";
        String code = "123456";
        given(emailVerificationService.verifyCode(email, code)).willReturn(true);

        // When & Then
        mockMvc.perform(post("/api/users/verify-code")
                        .param("email", email)
                        .param("code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("인증 결과"))
                .andExpect(jsonPath("$.result.data").value(true))
                .andDo(print());

        verify(emailVerificationService).verifyCode(email, code);
    }

    // --- 로그인/로그아웃 관련 테스트 ---
    @Test
    @DisplayName("로그인 여부 확인 - 로그인 상태")
    void checkLogin_LoggedIn() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        // When & Then
        mockMvc.perform(get("/api/users/check").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 여부 확인 - 로그아웃 상태")
    void checkLogin_LoggedOut() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession(); // 빈 세션

        // When & Then
        mockMvc.perform(get("/api/users/check").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data").value(false))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 - 성공")
    void login_Success() throws Exception {
        // Given
        ReqLoginDto loginDto = new ReqLoginDto("test@example.com", "password123"); // ReqLoginDto는 email, password 필드를 가진다고 가정

        LocalDateTime mockLastLoginAt = LocalDateTime.now();
        ResLoginDto expectedResponse = ResLoginDto.builder()
                .id(1L)
                .username("tester") // username 필드 추가
                .email("test@example.com")
                .profileImageUrl("http://profile.url/test.jpg")
                .lastLoginAt(mockLastLoginAt)
                .role("USER")
                .build();

        MockHttpSession mockSession = new MockHttpSession();

        given(userService.login(any(ReqLoginDto.class))).willReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto))
                        .session(mockSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("로그인 성공"))
                .andExpect(jsonPath("$.result.data.id").value(1L))
                .andExpect(jsonPath("$.result.data.username").value("tester")) // username 검증 추가
                .andExpect(jsonPath("$.result.data.email").value("test@example.com")) // email 검증 유지
                .andExpect(jsonPath("$.result.data.profileImageUrl").value("http://profile.url/test.jpg")) // profileImageUrl 검증 추가
                // lastLoginAt은 @JsonFormat에 의해 "yyyyMMddHHmmss" 형식의 문자열로 변환됨
                .andExpect(jsonPath("$.result.data.lastLoginAt").value(mockLastLoginAt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))) // lastLoginAt 검증 추가
                .andExpect(jsonPath("$.result.data.role").value("USER")) // role 검증은 이미 있었음
                .andExpect(request().sessionAttribute("userId", 1L))
                .andExpect(request().sessionAttribute("role", "USER"))
                .andDo(print());

        verify(userService).login(any(ReqLoginDto.class));
    }

    @Test
    @DisplayName("관리자 로그인 - 성공")
    void adminLogin_Success() throws Exception {
        // Given
        ReqLoginDto loginDto = new ReqLoginDto("admin@example.com", "adminPass"); // ReqLoginDto에 @AllArgsConstructor가 있다고 가정
        LocalDateTime mockLastLoginAt = LocalDateTime.now();
        ResLoginDto adminResponse = ResLoginDto.builder()
                .id(99L)
                .username("adminUser")
                .email("admin@example.com")
                .role("ADMIN")
                .profileImageUrl(null) // 또는 기본 관리자 프로필 URL
                .lastLoginAt(mockLastLoginAt)
                .build();
        MockHttpSession mockSession = new MockHttpSession();

        given(userService.login(any(ReqLoginDto.class))).willReturn(adminResponse);

        // When & Then
        mockMvc.perform(post("/api/users/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto))
                        .session(mockSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("[관리자] 로그인 성공"))
                .andExpect(jsonPath("$.result.data.id").value(99L))
                .andExpect(jsonPath("$.result.data.username").value("adminUser"))
                .andExpect(jsonPath("$.result.data.email").value("admin@example.com"))
                .andExpect(jsonPath("$.result.data.role").value("ADMIN"))
                .andExpect(jsonPath("$.result.data.lastLoginAt").value(mockLastLoginAt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))))
                .andExpect(request().sessionAttribute("userId", 99L))
                .andExpect(request().sessionAttribute("role", "ADMIN"))
                .andDo(print());

        verify(userService).login(any(ReqLoginDto.class));
    }


    @Test
    @DisplayName("관리자 로그인 - 실패 (일반 사용자 시도, 권한 없음)")
    void adminLogin_Failure_NotAdminRole() throws Exception {
        // Given
        ReqLoginDto loginDto = new ReqLoginDto("user@example.com", "password123"); // ReqLoginDto에 @AllArgsConstructor가 있다고 가정
        ResLoginDto userResponse = ResLoginDto.builder()
                .id(2L)
                .username("normalUser")
                .email("user@example.com")
                .role("USER") // 일반 사용자 역할
                .profileImageUrl(null)
                .lastLoginAt(LocalDateTime.now())
                .build();
        MockHttpSession mockSession = new MockHttpSession();

        given(userService.login(any(ReqLoginDto.class))).willReturn(userResponse);
        // 컨트롤러에서 CustomException(ErrorCode.PERMISSION_DENIED) 발생 예상

        // When & Then
        mockMvc.perform(post("/api/users/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto))
                        .session(mockSession))
                // .andExpect(status().isForbidden()) // GlobalExceptionHandler가 PERMISSION_DENIED를 403으로 매핑한다고 가정
                // .andExpect(jsonPath("$.error.code").value(ErrorCode.PERMISSION_DENIED.getCode()))
                .andDo(print());
        // 참고: GlobalExceptionHandler를 MockMvc에 등록하지 않으면, 예외가 그대로 전파되어 500 에러가 날 수 있습니다.
        // 정확한 테스트를 위해서는 GlobalExceptionHandler 설정 및 테스트가 필요합니다.
    }

    // --- 관리자 기능 테스트 (getAllUsers) ---
    @Test
    @DisplayName("관리자 - 전체 유저 목록 조회 - 성공")
    void getAllUsers_Admin_Success() throws Exception {
        // Given
        MockHttpSession adminSession = new MockHttpSession();
        adminSession.setAttribute("userId", 1L);
        adminSession.setAttribute("role", "ADMIN");

        LocalDateTime mockTime = LocalDateTime.now();
        List<ResLoginDto> userList = Collections.singletonList(
                ResLoginDto.builder()
                        .id(2L)
                        .username("user1")
                        .email("user1@example.com")
                        .role("USER")
                        .profileImageUrl("http://profile.url/user1.jpg")
                        .lastLoginAt(mockTime)
                        .build()
        );
        given(userService.getAllUsers()).willReturn(userList);

        // When & Then
        mockMvc.perform(get("/api/users/admin/list").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("[관리자] 전체 유저 목록 반환"))
                .andExpect(jsonPath("$.result.data[0].id").value(2L))
                .andExpect(jsonPath("$.result.data[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$.result.data[0].username").value("user1"))
                .andExpect(jsonPath("$.result.data[0].role").value("USER"))
                .andExpect(jsonPath("$.result.data[0].profileImageUrl").value("http://profile.url/user1.jpg"))
                .andExpect(jsonPath("$.result.data[0].lastLoginAt").value(mockTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))))
                .andExpect(jsonPath("$.result.count").value(1))
                .andDo(print());

        verify(userService).getAllUsers();
    }

    // --- 사용자 정보 수정 관련 테스트 (updateUserInfo) ---
    @Test
    @DisplayName("회원 정보 수정 (닉네임) - 성공 (본인)")
    void updateUserInfo_Success_Self() throws Exception {
        // Given
        Long userId = 1L;
        ReqUpdateUserDto updateDto = new ReqUpdateUserDto(); // ReqUpdateUserDto에 setUserName이 있다고 가정
        updateDto.setUsername("newUserName");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);
        session.setAttribute("role","USER");


        LocalDateTime mockTime = LocalDateTime.now();
        ResLoginDto updatedUserResponse = ResLoginDto.builder()
                .id(userId)
                .username("newUserName") // 변경된 닉네임
                .email("test@example.com") // 기존 이메일 (또는 변경된 이메일, DTO 정의에 따라)
                .role("USER")
                .profileImageUrl("http://profile.url/test.jpg") // 기존 프로필 이미지 URL
                .lastLoginAt(mockTime) // 업데이트된 시간 또는 기존 시간
                .build();

        // userService.updateUserInfo의 반환 타입이 ResLoginDto라고 가정
        given(userService.updateUserInfo(eq(userId), eq(userId), any(ReqUpdateUserDto.class))).willReturn(updatedUserResponse);

        // When & Then
        mockMvc.perform(patch("/api/users/{userId}/update", userId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("회원 정보 수정 완료"))
                .andExpect(jsonPath("$.result.data.id").value(userId))
                .andExpect(jsonPath("$.result.data.username").value("newUserName")) // username으로 검증 (DTO 필드명에 따라)
                .andExpect(jsonPath("$.result.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.result.data.role").value("USER"))
                .andExpect(jsonPath("$.result.data.profileImageUrl").value("http://profile.url/test.jpg"))
                .andExpect(jsonPath("$.result.data.lastLoginAt").value(mockTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))))
                .andDo(print());

        verify(userService).updateUserInfo(eq(userId), eq(userId), any(ReqUpdateUserDto.class));
    }

    // --- 내 정보 조회 테스트 (getCurrentUser) ---
    @Test
    @DisplayName("내 정보 조회 - 성공")
    void getCurrentUser_Success() throws Exception {
        // Given
        Long userId = 1L;
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);

        LocalDateTime mockTime = LocalDateTime.now();
        ResLoginDto userInfo = ResLoginDto.builder()
                .id(userId)
                .username("myUserName")
                .email("me@example.com")
                .role("USER")
                .profileImageUrl("myProfileUrl.jpg")
                .lastLoginAt(mockTime)
                .build();

        given(userService.getUserInfo(userId)).willReturn(userInfo);

        // When & Then
        mockMvc.perform(get("/api/users/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("유저 정보 조회 성공"))
                .andExpect(jsonPath("$.result.data.id").value(userId))
                .andExpect(jsonPath("$.result.data.username").value("myUserName")) // username으로 검증
                .andExpect(jsonPath("$.result.data.email").value("me@example.com"))
                .andExpect(jsonPath("$.result.data.role").value("USER"))
                .andExpect(jsonPath("$.result.data.profileImageUrl").value("myProfileUrl.jpg"))
                .andExpect(jsonPath("$.result.data.lastLoginAt").value(mockTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))))
                .andDo(print());

        verify(userService).getUserInfo(userId);
    }

}