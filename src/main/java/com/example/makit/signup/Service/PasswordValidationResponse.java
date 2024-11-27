package com.example.makit.signup.Service;

import lombok.Data;

//data 어노테이션 적용 이슈로 인한 getter, setter 직접 명시
@Data
public class PasswordValidationResponse {
    private boolean isPasswordValid;
    private String passwordErrorMessage;
    private boolean isPasswordMatch;
    private String passwordMatchErrorMessage;

    // Getter, Setter 추가
    public boolean isPasswordValid() {
        return isPasswordValid;
    }

    public void setIsPasswordValid(boolean isPasswordValid) {
        this.isPasswordValid = isPasswordValid;
    }

    public String getPasswordErrorMessage() {
        return passwordErrorMessage;
    }

    public void setPasswordErrorMessage(String passwordErrorMessage) {
        this.passwordErrorMessage = passwordErrorMessage;
    }

    public boolean isPasswordMatch() {
        return isPasswordMatch;
    }

    public void setIsPasswordMatch(boolean isPasswordMatch) {
        this.isPasswordMatch = isPasswordMatch;
    }

    public String getPasswordMatchErrorMessage() {
        return passwordMatchErrorMessage;
    }

    public void setPasswordMatchErrorMessage(String passwordMatchErrorMessage) {
        this.passwordMatchErrorMessage = passwordMatchErrorMessage;
    }

    public void setRedirectToNicknamePage(boolean b) {
    }
}