package com.example.makit.signup.DTO;

import lombok.Data;

import java.util.List;

@Data
public class SignupRequestDTO {
    private String password;           // 사용자가 입력한 비밀번호
    private String confirmPassword;    // 사용자가 입력한 비밀번호 확인
    private boolean showPassword;      // 비밀번호 보기/숨기기
    private String nickname;
    private boolean isNicknameValid;   // 닉네임 유효성 검사 결과

    private String phoneNumber;

    private List<String> selectedFields;  // 사용자가 선택한 분야 '이름' 리스트
    private List<String> selectedGenres;  // 사용자가 선택한 장르 '이름' 리스트
}
