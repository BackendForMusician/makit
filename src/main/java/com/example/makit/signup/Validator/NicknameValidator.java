package com.example.makit.signup.Validator;

import java.util.regex.Pattern;

public class NicknameValidator {
    // 닉네임 조건: 2~20자의 영문 대/소문자, 숫자, 특수문자. figma에 따른 규칙 적용
    private static final String NICKNAME_REGEX = "^[a-zA-Z0-9!@#$%^&*()_+=-]{2,20}$";

    public static boolean isValidNickname(String nickname) {
        return nickname != null && Pattern.matches(NICKNAME_REGEX, nickname);
    }
}
