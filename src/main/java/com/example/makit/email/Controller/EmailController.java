package com.example.makit.email.Controller;

import com.example.makit.email.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/email")  // 기본 URL 경로 설정
public class EmailController {

    @Autowired
    private EmailService emailService;

    // 이메일 인증번호 전송 API
    @PostMapping("/send")  // /api/email/send 경로로 POST 요청을 처리
    public Map<String, String> sendVerificationCode(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        try {
            // 서비스 호출하여 인증번호 생성 및 전송
            emailService.sendVerificationEmail(email);
            response.put("message", "Verification email sent successfully.");
        } catch (Exception e) {
            response.put("message", "Failed to send email: " + e.getMessage());
        }
        return response;
    }
}