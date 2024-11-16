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


    @PostMapping("/send")
    public Map<String, String> sendVerificationCode(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        try {

            emailService.sendVerificationEmail(email);
            response.put("message", "Verification email sent successfully.");
        } catch (Exception e) {
            response.put("message", "Failed to send email: " + e.getMessage());
        }
        return response;
    }
}