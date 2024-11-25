package com.example.makit.login.Filter;

import com.example.makit.login.Auth.CustomAuthenticationToken;
import com.example.makit.login.Auth.CustomUserDetails;
import com.example.makit.signup.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // 로그인 요청에서 이메일(email)과 비밀번호(password) 가져오기
        String email = authentication.getName(); // 이메일
        String password = (String) authentication.getCredentials();

        // UserDetailsService에서 사용자 정보 로드
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
        UserEntity entity = userDetails.getUserEntity(); // UserEntity를 CustomUserDetails에서 가져옴

        if(!passwordEncoder.matches(password, entity.getPassword())) {
            throw new BadCredentialsException("Invalid Password");
        }
        // 모든 사용자에게 기본 권한 ROLE_USER 부여
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new CustomAuthenticationToken(userDetails, null,authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(CustomAuthenticationToken.class);
    }
}