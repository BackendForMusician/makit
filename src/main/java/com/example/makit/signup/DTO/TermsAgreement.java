package com.example.makit.signup.DTO;

import lombok.*;

// 예시: DB 저장 및 내부 도메인 로직 용도의 클래스
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TermsAgreement {
    private Boolean isOver14;
    private Boolean termsOfService;
    private Boolean privacyConsent;
    private Boolean privacyPolicy;
    private Boolean optionalPrivacyConsent;

    // 기본 생성자, 모든 필드 생성자, getter/setter 등
}
