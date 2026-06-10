package com.guardians.domain.user.port;

public interface EmailVerificationPort {
    void sendVerificationCode(String email, String templatePath);
    boolean verifyCode(String email, String code);
}
