package com.guardians.service.auth;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    @Async
    public void sendVerificationCode(String email, String templatePath) {
        String code = generateCode();
        String htmlContent = loadTemplate(templatePath, code); // 🔥 여기서 사용

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Guardians 이메일 인증 코드");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES);

        } catch (MessagingException e) {
            System.err.println("이메일 전송 실패: " + e.getMessage());
        }
    }

    public void sendVerificationCode(String email) {
        sendVerificationCode(email, "mail/signup-verification.html"); // 기본 템플릿
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(email);
        return code.equals(storedCode);
    }

    private String generateCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    private String loadTemplate(String path, String code) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new IllegalArgumentException("템플릿 파일 없음: " + path);
            String html = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return html.replace("%CODE%", code);
        } catch (IOException e) {
            throw new RuntimeException("템플릿 읽기 실패", e);
        }
    }
}
