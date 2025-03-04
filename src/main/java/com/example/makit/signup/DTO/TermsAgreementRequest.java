package com.example.makit.signup.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


public class TermsAgreementRequest {

    @NotNull(message = "isOver14 필드는 필수입니다.")
    private Boolean isOver14; // 14세 이상 여부

    @NotNull(message = "termsOfService 필드는 필수입니다.")
    private Boolean termsOfService; // 서비스 이용 약관 동의 여부

    @NotNull(message = "privacyConsent 필드는 필수입니다.")
    private Boolean privacyConsent; // 개인정보 수집 및 이용 동의 여부

    @NotNull(message = "privacyPolicy 필드는 필수입니다.")
    private Boolean privacyPolicy; // 개인정보 보호 정책 동의 여부

    private Boolean optionalPrivacyConsent; // 선택적 개인정보 수집 및 이용 동의 여부

    // 기본 생성자
    public TermsAgreementRequest() {
    }

    // 모든 필드를 포함한 생성자
    public TermsAgreementRequest(Boolean isOver14, Boolean termsOfService, Boolean privacyConsent, Boolean privacyPolicy, Boolean optionalPrivacyConsent) {
        this.isOver14 = isOver14;
        this.termsOfService = termsOfService;
        this.privacyConsent = privacyConsent;
        this.privacyPolicy = privacyPolicy;
        this.optionalPrivacyConsent = optionalPrivacyConsent;
    }

    // Getter & Setter
    public Boolean getIsOver14() {
        return isOver14;
    }

    public void setIsOver14(Boolean isOver14) {
        this.isOver14 = isOver14;
    }

    public Boolean getTermsOfService() {
        return termsOfService;
    }

    public void setTermsOfService(Boolean termsOfService) {
        this.termsOfService = termsOfService;
    }

    public Boolean getPrivacyConsent() {
        return privacyConsent;
    }

    public void setPrivacyConsent(Boolean privacyConsent) {
        this.privacyConsent = privacyConsent;
    }

    public Boolean getPrivacyPolicy() {
        return privacyPolicy;
    }

    public void setPrivacyPolicy(Boolean privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }

    public Boolean getOptionalPrivacyConsent() {
        return optionalPrivacyConsent;
    }

    public void setOptionalPrivacyConsent(Boolean optionalPrivacyConsent) {
        this.optionalPrivacyConsent = optionalPrivacyConsent;
    }
}
