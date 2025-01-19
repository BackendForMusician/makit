package com.example.makit.findid.Validator;

public class FindIdValidator {

    // 전화번호 유효성 검사 메서드
    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^[0-9]{11}$");
    }
}
