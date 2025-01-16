package com.example.makit.login.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserDTO {
    private String email;       // 이메일 (로그인 ID)
    private String password;    // 비밀번호 (인증 용도)

    public LoginUserDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
