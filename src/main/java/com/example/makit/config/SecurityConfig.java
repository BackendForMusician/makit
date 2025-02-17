package com.example.makit.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 최신 방식으로 CSRF 비활성화
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)) //세션 기반 로그인에선 항상 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( // 커스텀 필터 -> 시큐리티 필터 순으로 작동하므로 여기선 모든 경로에 대한 permitALL 필요
                                "/api/email/send",
                                "/api/email/resend",
                                "/api/reset/**",
                                "/api/email/validate",
                                "/api/email/session",
                                "/api/signup/**",
                                "/h2-console/**", // H2 콘솔 경로 허용 나중에 삭제
                                "/api/auth/**", // 로그인, 로그아웃
                                "/api/profile/**",
                                "/error"
                                ).permitAll()
                                                .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable()) // H2 콘솔에서 iframe 사용 허용
                ); // 접근 거부 핸들러;

        return http.build();
    }


}
