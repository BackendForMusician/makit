package com.example.makit.signup.DTO;

import lombok.Data;

@Data
public class SignupRequestDTO {
    private String password;           // 사용자가 입력한 비밀번호
    private String confirmPassword;    // 사용자가 입력한 비밀번호 확인
    private boolean showPassword;      // 비밀번호 보기/숨기기
}
