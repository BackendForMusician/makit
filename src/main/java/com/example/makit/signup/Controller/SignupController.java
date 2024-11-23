package com.example.makit.signup.Controller;

import com.example.makit.signup.DTO.FieldsGenresResponseDTO;
import com.example.makit.signup.DTO.SignupRequestDTO;
import com.example.makit.signup.Service.FieldService;
import com.example.makit.signup.Service.GenreService;
import com.example.makit.signup.Service.PasswordValidationResponse;
import com.example.makit.signup.Service.SignupService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/signup")
@RequiredArgsConstructor
public class SignupController {

    private final SignupService signupService;
    private final FieldService fieldService;
    private final GenreService genreService;

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

    // 세션에서 비밀번호 확인. 포스트맨에서 검사를 위함
    @GetMapping("/session-password")
    public ResponseEntity<String> getSessionPassword(HttpSession session) {
        String password = (String) session.getAttribute("hashedPassword");
        if (password != null) {
            return ResponseEntity.ok("세션에 저장된 비밀번호: " + password);
        } else {
            return ResponseEntity.badRequest().body("세션에 비밀번호가 없습니다.");
        }
    }

    // nicknmae 검사 api
    @PostMapping("/nickname")
    public ResponseEntity<String> validateNickname(@RequestBody SignupRequestDTO request, HttpSession session) {
        String nickname = request.getNickname();
        boolean isNicknameValid = signupService.validateAndSaveNickname(nickname);

        if (isNicknameValid) {
            return ResponseEntity.ok("닉네임이 유효합니다.");
        } else {
            return ResponseEntity.badRequest().body("닉네임은 2~20자의 대/소문자, 숫자, 특수문자만 허용됩니다.");
        }
    }


    //postman에서 nickname 세션 저장 검사
    @GetMapping("/session-nickname")
    public ResponseEntity<String> getSessionNickname(HttpSession session) {
        String nickname = (String) session.getAttribute("nickname");
        if (nickname != null) {
            return ResponseEntity.ok("세션에 저장된 닉네임: " + nickname);
        } else {
            return ResponseEntity.badRequest().body("세션에 닉네임이 없습니다. 저장 오류 발생입니다.");
        }
    }

    //phonenumber 검사 api
    @PostMapping("/phone-number")
    public ResponseEntity<String> validatePhoneNumber(@RequestBody SignupRequestDTO request) {
        String phoneNumber = request.getPhoneNumber();
        boolean isPhoneNumberValid = signupService.validateAndSavePhoneNumber(phoneNumber);

        if (isPhoneNumberValid) {
            return ResponseEntity.ok("전화번호가 유효합니다.");
        } else {
            return ResponseEntity.badRequest().body("숫자만 입력.");
        }
    }

    //postman에서 session에 저장되는 Phonenumber검사
    @GetMapping("/session-phone-number")
    public ResponseEntity<String> getSessionPhoneNumber(HttpSession session) {
        String phoneNumber = (String) session.getAttribute("phoneNumber");
        if (phoneNumber != null) {
            return ResponseEntity.ok("세션에 저장된 전화번호: " + phoneNumber);
        } else {
            return ResponseEntity.badRequest().body("세션에 전화번호가 없습니다.");
        }
    }

    // 전화번호 입력 후 분야와 장르 리스트를 반환하는 api
    @GetMapping("/fields-genres")
    public ResponseEntity<FieldsGenresResponseDTO> getAvailableFieldsAndGenres() {
        // FieldService와 GenreService를 사용해 분야와 장르의 이름만 리스트로 가져옴
        List<String> fields = fieldService.getAllFieldNames();
        List<String> genres = genreService.getAllGenreNames();

        FieldsGenresResponseDTO response = new FieldsGenresResponseDTO();
        response.setFields(fields);
        response.setGenres(genres);

        return ResponseEntity.ok(response);
    }

    // 최종 회원가입 API - 분야와 장르를 선택하고 모든 회원가입 절차를 완료하는 API
    @PostMapping("/complete")
    public ResponseEntity<String> completeSignup(@RequestBody SignupRequestDTO request) {
        boolean success = signupService.completeSignup(request.getSelectedFields(), request.getSelectedGenres());
        if (success) {
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("분야와 장르 선택이 유효하지 않습니다. 최소 1개, 최대 3개를 선택해주세요.");
        }
    }



}
