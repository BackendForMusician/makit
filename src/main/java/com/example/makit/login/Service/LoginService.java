package com.example.makit.login.Service;

import com.example.makit.signup.Entity.UserEntity;
import com.example.makit.signup.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<UserEntity> authenticate(String email, String password) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return userOpt; // 인증 성공
        }
        return Optional.empty(); // 인증 실패
    }
}
