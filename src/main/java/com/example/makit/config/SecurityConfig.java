package com.example.makit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 최신 방식으로 CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/email/send", "/api/email/resend", "/api/email/validate", "/api/email/session","/api/signup/**").permitAll() // send,resend,signup관련 허용
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                );
        return http.build();
    }

}
