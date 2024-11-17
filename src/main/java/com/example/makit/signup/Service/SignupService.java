package com.example.makit.signup.Service;

import com.example.makit.signup.DTO.SignupRequestDTO;
import com.example.makit.signup.Validator.PasswordValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class SignupService {

    // 비밀번호 암호화를 위한 BCryptPasswordEncoder 객체 생성
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // HttpSession 객체 주입 받기 (세션 관리)
    private final HttpSession session;

    // 생성자 주입을 통해 HttpSession 객체를 받을 수 있도록 설정
    public SignupService(HttpSession session) {
        this.session = session;
    }

    // 비밀번호 유효성 검사 및 비밀번호 해싱
    public PasswordValidationResponse validatePasswords(SignupRequestDTO request) {
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        PasswordValidationResponse response = new PasswordValidationResponse();

        // 비밀번호 형식 검증
        if (!PasswordValidator.isValidPassword(password)) {
            response.setIsPasswordValid(false); // 비밀번호 유효성 실패
            response.setPasswordErrorMessage("비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자를 포함해야 합니다.");
        } else {
            response.setIsPasswordValid(true); // 비밀번호 유효성 성공
        }

        // 비밀번호 일치 여부 검증
        if (!password.equals(confirmPassword)) {
            response.setIsPasswordMatch(false); // 비밀번호 불일치
            response.setPasswordMatchErrorMessage("다시 입력해주세요.");
        } else {
            response.setIsPasswordMatch(true); // 비밀번호 일치
        }

        // 비밀번호가 유효하고, 비밀번호 확인이 일치하면 비밀번호 해싱 및 세션에 저장
        if (response.isPasswordValid() && response.isPasswordMatch()) {
            // 비밀번호 해싱
            String hashedPassword = passwordEncoder.encode(password);

            // 세션에 해싱된 비밀번호 저장 (임시 저장)
            session.setAttribute("hashedPassword", hashedPassword);
        }

        return response;
    }
}
