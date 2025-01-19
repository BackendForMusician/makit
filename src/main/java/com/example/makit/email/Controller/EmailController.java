package com.example.makit.email.Controller;

import com.example.makit.email.Dto.EmailRequestDTO;
import com.example.makit.email.Dto.EmailStatusResponseDTO;
import com.example.makit.email.Dto.VerificationCodeRequestDTO;
import com.example.makit.email.Service.EmailService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    // 이메일 인증번호 발송
    @PostMapping("/send")
    public ResponseEntity<EmailStatusResponseDTO> sendVerificationCode(
            @Valid @RequestBody EmailRequestDTO request, HttpSession session) {

        String email = request.getEmail();
        String authCode = emailService.generateVerificationCode();

        // 세션에 이메일, 인증번호, 인증시간 저장. 유효시간 초과 및 인증번호 일치여부 검사하기 위함
        session.setAttribute("email", email);
        session.setAttribute("authCode", authCode);
        session.setAttribute("timer", System.currentTimeMillis());

        emailService.sendVerificationEmail(email, authCode);

        // 초기 인증 상태 전달
        return ResponseEntity.ok(new EmailStatusResponseDTO(
                true,     // 이메일 유효성 검사 상태
                true,     // 인증 단계 여부 (현재 인증 단계임)
                false,    // 비밀번호 설정 단계 여부
                false,    // 인증 완료 여부
                true,    // 인증번호 틀림 여부. 초기에는 틀렸다고 설정
                180       // 타이머: 3분(180초) 해당 값을 받아서 프론트에서 작업을 통해 잔여시간 보여줄 수 있음.
        ));
    }

    // 인증번호 검증
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateVerificationCode(
            @RequestBody VerificationCodeRequestDTO request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String email = (String) session.getAttribute("email");
        String sessionAuthCode = (String) session.getAttribute("authCode");
        Long timer = (Long) session.getAttribute("timer");

        if (email == null || sessionAuthCode == null || timer == null) {
            response.put("message", "인증 정보가 없습니다. 다시 시도해주세요.");
            response.put("isAuthStep", true);
            response.put("isAuthValid", false);
            response.put("isAuthIncorrect", true);
            response.put("isPasswordStep", false);  // 비밀번호 설정 단계 아니므로 false
            response.put("timer", 180);  // 인증시간이 초과될 때는 0으로 설정할 수 있음.
            return ResponseEntity.status(400).body(response);
        }

        // 인증시간이 초과된 경우 (3분 이상 경과)
        if (System.currentTimeMillis() - timer > 180_000) { // 3분 제한
            response.put("message", "인증 시간이 초과되었습니다.");
            response.put("isAuthStep", true); // 인증 중
            response.put("isAuthValid", false); // 인증 유효하지 않음
            response.put("isAuthIncorrect", true); // 인증번호가 틀린 것처럼 처리
            response.put("isPasswordStep", false); // 비밀번호 설정 단계로 못 넘어감
            response.put("timer", 0);  // 인증시간이 초과되면 타이머는 0
            return ResponseEntity.status(400).body(response);
        }

        // 인증번호 및 시간 유효성 검사
        if (!email.equals(request.getEmail()) || !sessionAuthCode.equals(request.getAuthCode())) {
            response.put("message", "인증번호가 틀렸습니다.");
            session.setAttribute("isAuthValid", false); // 인증번호가 틀린 경우
            response.put("isAuthStep", true); // 인증 중
            response.put("isAuthValid", false);
            response.put("isAuthIncorrect", true); // 인증번호가 틀린 경우
            response.put("isPasswordStep", false);  // 비밀번호 설정 단계 아니므로 false
            response.put("timer", 180); // 남은 시간
            return ResponseEntity.status(400).body(response);
        }

        // 인증 성공
        session.setAttribute("isAuthValid", true); // 인증 유효
        session.setAttribute("isPasswordStep", true); // 비밀번호 설정 단계로 이동 가능
        response.put("message", "인증 성공");
        response.put("isAuthStep", false); // 인증 완료 후 인증 단계 종료
        response.put("isAuthValid", true);
        response.put("isAuthIncorrect", false); // 인증번호가 맞음
        response.put("isPasswordStep", true);  // 비밀번호 설정 단계로 넘어갈 수 있도록 명시
        response.put("timer", 0);  // 인증이 완료되면 타이머는 0. 프론트엔드에서 처리하는 파트
        return ResponseEntity.ok(response);
    }



    // 인증번호 재전송
    @PostMapping("/resend")
    public ResponseEntity<EmailStatusResponseDTO> resendVerificationCode(HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            return ResponseEntity.status(400).body(new EmailStatusResponseDTO(
                    false,    // 이메일 유효성
                    false,    // 인증 단계
                    false,    // 비밀번호 단계
                    false,    // 인증 성공 여부
                    true,    // 인증번호 틀림 여부
                    0         // 타이머
            ));
        }

        String newCode = emailService.generateVerificationCode();
        session.setAttribute("authCode", newCode);
        session.setAttribute("timer", System.currentTimeMillis()); // 타이머 초기화
        emailService.resendVerificationEmail(email, newCode);

        // 새로운 인증 상태 반환
        return ResponseEntity.ok(new EmailStatusResponseDTO(
                true,     // 이메일 유효성
                true,     // 인증 단계
                false,    // 비밀번호 단계
                false,    // 인증 성공 여부
                true,    // 인증번호 틀림 여부
                180       // 타이머: 3분
        ));
    }

    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> getSessionData(HttpSession session) {
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("email", session.getAttribute("email"));
        sessionData.put("authCode", session.getAttribute("authCode"));
        sessionData.put("isAuthValid", session.getAttribute("isAuthValid"));
        sessionData.put("isPasswordStep", session.getAttribute("isPasswordStep"));

        return ResponseEntity.ok(sessionData);
    }

    // 이메일 인증번호 발송

}