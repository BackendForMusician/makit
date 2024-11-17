package com.example.makit.signup.Controller;

import com.example.makit.signup.DTO.SignupRequestDTO;
import com.example.makit.signup.Service.PasswordValidationResponse;
import com.example.makit.signup.Service.SignupService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/signup")
@RequiredArgsConstructor
public class SignupController {

    private final SignupService signupService;

    // 비밀번호 입력 및 검증. /api/signup/password 요청에 대해 비밀번호 유효성 및 확인란 일치 여부를 검사하는 API
    @PostMapping("/password")
    public ResponseEntity<PasswordValidationResponse> validatePassword(@RequestBody SignupRequestDTO request) {
        try {
            PasswordValidationResponse response = signupService.validatePasswords(request);
            return ResponseEntity.ok(response); // 유효성 검사 결과를 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // 예외 처리 (필요에 따라 메시지 추가)
        }
    }

    // 세션에서 비밀번호 확인
    @GetMapping("/session-password")
    public ResponseEntity<String> getSessionPassword(HttpSession session) {
        String password = (String) session.getAttribute("hashedPassword");
        if (password != null) {
            return ResponseEntity.ok("세션에 저장된 비밀번호: " + password);
        } else {
            return ResponseEntity.badRequest().body("세션에 비밀번호가 없습니다.");
        }
    }
}
