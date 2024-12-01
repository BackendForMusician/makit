package com.example.makit.findid.Controller;

import com.example.makit.findid.DTO.FindIdRequestDTO;
import com.example.makit.findid.Service.FindIdService;
import com.example.makit.findid.Validator.FindIdValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/findid")
public class FindIdController {

    private final FindIdService findIdService;

    public FindIdController(FindIdService findIdService) {
        this.findIdService = findIdService;
    }

    @PostMapping("/check")
    public ResponseEntity<?> findIdByPhoneNumber(@RequestBody FindIdRequestDTO request) {
        String phoneNumber = request.getPhoneNumber();

        // 전화번호 유효성 검사
        if (!FindIdValidator.isValidPhoneNumber(phoneNumber)) {
            return ResponseEntity.badRequest().body("숫자만 입력해주세요.");
        }

        // 전화번호로 이메일 찾기
        String email = findIdService.findEmailByPhoneNumber(phoneNumber);
        if (email == null) {
            return ResponseEntity.badRequest().body("존재하지 않는 계정입니다.");
        }

        return ResponseEntity.ok("회원님의 아이디는 " + email + " 입니다.");
    }
}
