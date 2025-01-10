package com.example.makit.resetPassword.Controller;

import com.example.makit.email.Dto.EmailRequestDTO;
import com.example.makit.resetPassword.Service.ResetPasswordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reset")
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;

    public ResetPasswordController(ResetPasswordService resetPasswordService) {
        this.resetPasswordService = resetPasswordService;
    }

    @PostMapping("/sendLink")
    public ResponseEntity<String> sendResetLink(@Valid @RequestBody EmailRequestDTO request) {
        String email = request.getEmail();

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return ResponseEntity.status(400).body("유효하지 않은 이메일 형식입니다.");
        }

        try {
            boolean exists = resetPasswordService.checkEmailExists(email).get();
            if (exists) {
                String uniqueLink = resetPasswordService.generateAndStoreResetPasswordLink(email);
                resetPasswordService.sendResetPasswordLink(email, uniqueLink);
                return ResponseEntity.ok("비밀번호 재설정 링크가 이메일로 전송되었습니다.");
            } else {
                return ResponseEntity.status(400).body("이메일이 존재하지 않습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateResetLink(@RequestParam String email, @RequestParam String link) {
        boolean isValid = resetPasswordService.validateResetLink(email, link);
        if (isValid) {
            return ResponseEntity.ok("링크가 유효합니다.");
        } else {
            return ResponseEntity.status(400).body("링크가 유효하지 않거나 만료되었습니다.");
        }
    }
}
