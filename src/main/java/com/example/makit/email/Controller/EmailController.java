package com.example.makit.email.Controller;

import com.example.makit.email.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;


    // 인증번호 발송
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendVerificationCode(@RequestParam String email) {

        Map<String, String> response = new HashMap<>();
        try {
            emailService.sendVerificationEmail(email);
            response.put("message", "이메일이 전송되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());  // 오류 발생 시 반환되는 메시지. 필요한 값 추후 작성 가능
            return ResponseEntity.status(400).body(response);
        }
    }

    // 인증번호 유효성 검사
    @PostMapping("/validate")
    public ResponseEntity<Map<String, String>> validateVerificationCode(
            @RequestParam String email,
            @RequestParam String code) {
        Map<String, String> response = new HashMap<>();
        try {
            if (emailService.validateVerificationCode(email, code)) {
                response.put("message", "인증에 성공하였습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "인증번호가 틀렸습니다. 다시 입력해주세요.");
                return ResponseEntity.status(400).body(response);
            }
        } catch (IllegalStateException e) {
            response.put("message", e.getMessage());  // 인증번호 유효시간 초과
            return ResponseEntity.status(400).body(response);
        }
    }
    // 인증 상태 확인. 프론트엔드에서 비밀번호 입력 페이지로 넘어가기 전에 /api/email/verify-status를 호출하여 인증 상태를 확인 가능
    @GetMapping("/verify-status")
    public ResponseEntity<Map<String, String>> checkVerificationStatus(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean isVerified = emailService.checkVerificationStatus(email);
            if (isVerified) {
                response.put("message", "이메일 인증이 완료되었습니다.");;
                //redirection하는 endpoint지정 필요
                //혹은 session에 상태저장을 통해 프론트엔드 측에서 redirection 가능
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "이메일 인증이 완료되지 않았습니다.");
                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }


    // 인증번호 재전송
    @PostMapping("/resend")
    public ResponseEntity<Map<String, String>> resendVerificationCode(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        try {
            emailService.resendVerificationCode(email);
            response.put("message", "새로운 인증 이메일이 전송되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());  // 오류 발생 시 반환되는 메시지
            return ResponseEntity.status(400).body(response);
        }
    }
}
