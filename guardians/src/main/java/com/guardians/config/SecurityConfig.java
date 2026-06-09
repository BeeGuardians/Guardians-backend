package com.guardians.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF 토큰 핸들러 설정 (SPA 환경용)
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName(null); // 토큰을 lazy하게 로드하지 않음

        return http
                .cors(Customizer.withDefaults())
                // CSRF 활성화 - SPA 환경에서는 쿠키 기반 토큰 사용
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(requestHandler)
                        // 인증 관련 엔드포인트는 CSRF 예외 처리
                        .ignoringRequestMatchers(
                                "/api/users/login",
                                "/api/users/signup",
                                "/api/users/logout"
                        )
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authorizeHttpRequests(auth -> auth
                        // 명시적으로 공개 엔드포인트 지정 (보안 강화)
                        .requestMatchers(
                                "/api/users/login",
                                "/api/users/signup",
                                "/api/users/logout",
                                "/api/users/check-email",
                                "/api/users/check-username"
                        ).permitAll()
                        // 조회성 API는 공개
                        .requestMatchers(
                                "/api/boards/**",
                                "/api/wargames/**",
                                "/api/questions/**",
                                "/api/ranking/**"
                        ).permitAll()
                        // Swagger/API 문서
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // 관리자 API는 인증 필요
                        .requestMatchers("/api/admin/**").authenticated()
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                )
                .build();
    }
}
