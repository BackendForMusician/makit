package com.example.makit.email.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;  // JavaMailSender 주입

    // 인증번호 생성 및 전송
    public void sendVerificationEmail(String email) throws MessagingException {
        // 6자리 인증번호 생성
        String verificationCode = generateVerificationCode();

        // 이메일 전송
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);  // true는 HTML 메시지 처리

        helper.setTo(email);
        helper.setSubject("Your Verification Code");
        helper.setText("<p>Your verification code is: <strong>" + verificationCode + "</strong></p>", true);

        mailSender.send(message);  // 이메일 전송
    }

    // 인증번호 생성
    private String generateVerificationCode() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }
}
