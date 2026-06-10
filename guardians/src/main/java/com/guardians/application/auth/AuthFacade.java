package com.guardians.application.auth;

import com.guardians.domain.user.port.EmailVerificationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final EmailVerificationPort emailVerificationPort;

    public void sendVerificationCode(String email, String templatePath) {
        emailVerificationPort.sendVerificationCode(email, templatePath);
    }

    public void sendVerificationCode(String email) {
        emailVerificationPort.sendVerificationCode(email, "mail/signup-verification.html");
    }

    public boolean verifyCode(String email, String code) {
        return emailVerificationPort.verifyCode(email, code);
    }
}
