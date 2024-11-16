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
    private JavaMailSender mailSender;


    public void sendVerificationEmail(String email) throws MessagingException {

        String verificationCode = generateVerificationCode();

        // 이메일 전송
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email);
        helper.setSubject("Your Verification Code");
        helper.setText("<p>Your verification code is: <strong>" + verificationCode + "</strong></p>", true);

        mailSender.send(message);
    }


    private String generateVerificationCode() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }
}
