package com.guardians.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    public void sendVerificationCode(String email) {
        String code = generateCode(); // 랜덤 생성
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Guardians 이메일 인증 코드");
        message.setText("인증코드: " + code);
        mailSender.send(message);

        redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES);
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(email);
        return code.equals(storedCode);
    }

    private String generateCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
}
