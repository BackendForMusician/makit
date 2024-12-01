package com.example.makit.config;

import com.example.makit.login.Entrypoint.CustomLoginAuthenticationEntryPoint;
import com.example.makit.login.Filter.CustomAuthenticationFilter;
import com.example.makit.login.Handler.CustomAccessDeniedHandler;
import com.example.makit.login.Handler.CustomAuthenticationFailureHandler;
import com.example.makit.login.Handler.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomLoginAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationManager authenticationManager) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = customAuthenticationFilter(authenticationManager);
        http
                .csrf(csrf -> csrf.disable()) // 최신 방식으로 CSRF 비활성화
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)) //세션 기반 로그인에선 항상 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/email/send",
                                "/api/email/resend",
                                "/api/email/validate",
                                "/api/email/session",
                                "/api/findid/**",
                                "/api/signup/**",
                                "/h2-console/**", // H2 콘솔 경로 허용 나중에 삭제
                                "/api/login", // 로그인, 로그아웃
                                "/api/logout").permitAll()
                                                .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable()) // H2 콘솔에서 iframe 사용 허용
                )
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 커스텀 필터 등록
                .exceptionHandling(config -> config
                        .authenticationEntryPoint(authenticationEntryPoint) // 인증 실패 핸들러
                        .accessDeniedHandler(accessDeniedHandler)); // 접근 거부 핸들러;

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager(); // AuthenticationManager 빈 등록
    }

    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter(AuthenticationManager authenticationManager) {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter();
        customAuthenticationFilter.setAuthenticationManager(authenticationManager); // 인증 매니저 설정
        customAuthenticationFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler); // 성공 핸들러
        customAuthenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler); // 실패 핸들러
        return customAuthenticationFilter;
    }

}
