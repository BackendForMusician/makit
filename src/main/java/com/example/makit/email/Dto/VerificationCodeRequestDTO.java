package com.example.makit.email.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class VerificationCodeRequestDTO {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.") //버튼 비활성화 방식 채택 시 삭제 가능 메세지
    private String email;

    @NotBlank(message = "인증 코드는 필수 입력 항목입니다.") //버튼 비활성화 방식 채택 시 삭제 가능 메세지
    private String authCode;
}