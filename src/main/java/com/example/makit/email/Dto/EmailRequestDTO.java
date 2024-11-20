package com.example.makit.email.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailRequestDTO {

    @NotBlank(message = "이메일을 입력하세요.") //notblank로 입력강제. 메세지는 프론트엔드 담당. 비활성화 방식 채택 시 삭제 가능 메세지
    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    private String email;
}