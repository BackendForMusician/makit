package com.example.makit.email.Service;

import com.example.makit.email.Entity.EmailEntity;
import com.example.makit.email.Exception.CustomException;
import com.example.makit.email.Repository.EmailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    EmailRepository emailRepository;

    public EmailService(JavaMailSender mailSender, EmailRepository emailRepository) {
        this.mailSender = mailSender;
        this.emailRepository = emailRepository;
    }

    // 인증번호 생성
    public String generateVerificationCode() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000)); // 6자리 인증번호
    }

    @Async
    public void sendVerificationEmail(String email, String code) {

        EmailEntity existingEmail = emailRepository.findByEmail(email).orElse(null);
        if (existingEmail != null && existingEmail.isVerified()) {
            throw new CustomException("이미 가입된 이메일입니다.");  // 이메일 발송 안하고 에러 메시지 반환. 프론트에 넘겨줄 값
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("인증 코드");
            helper.setText("<p>인증 코드: <strong>" + code + "</strong></p>", true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new CustomException("이메일 발송 실패");
        }
    }
    @Async
    public void resendVerificationEmail(String email, String code) {
        EmailEntity existingEmail = emailRepository.findByEmail(email).orElse(null);
        if (existingEmail != null && existingEmail.isVerified()) {
            throw new CustomException("이미 가입된 이메일 입니다.");  // 이메일 발송 안하고 에러 메시지 반환. 프론트에 넘겨줄 값
        }
        try {
            // 새로운 인증 코드 발송
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("새로운 인증 코드");
            helper.setText("<p>새로운 인증 코드: <strong>" + code + "</strong></p>", true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new CustomException("이메일 발송 실패");
        }
    }
}