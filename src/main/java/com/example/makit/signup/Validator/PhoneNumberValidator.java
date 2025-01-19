package com.example.makit.signup.Validator;

public class PhoneNumberValidator {

    // 전화번호 유효성 검사 메서드
    public static boolean isValidPhoneNumber(String phoneNumber) {
        // 전화번호는 숫자만 포함. 길이는 10~11자리
        return phoneNumber != null && phoneNumber.matches("^[0-9]{11}$");
    }
}
