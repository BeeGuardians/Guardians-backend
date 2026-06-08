package com.guardians.application.auth;

import com.guardians.service.auth.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 인증 관련 유스케이스 조율 (이메일 인증코드 발송/검증)
 * EmailVerificationService는 @Service 직접 구현체이므로 그대로 위임한다.
 */
@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final EmailVerificationService emailVerificationService;

    public void sendVerificationCode(String email, String templatePath) {
        emailVerificationService.sendVerificationCode(email, templatePath);
    }

    public void sendVerificationCode(String email) {
        emailVerificationService.sendVerificationCode(email);
    }

    public boolean verifyCode(String email, String code) {
        return emailVerificationService.verifyCode(email, code);
    }
}
