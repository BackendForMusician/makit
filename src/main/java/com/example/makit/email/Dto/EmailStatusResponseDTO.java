package com.example.makit.email.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailStatusResponseDTO {
    private boolean isEmailValid;
    private boolean isAuthStep;
    private boolean isPasswordStep;
    private boolean isAuthValid;
    private boolean isAuthIncorrect = true; //초기에 잘못되었다고 설정해둬야됨. 헷갈림 방지
    private int timer;
}