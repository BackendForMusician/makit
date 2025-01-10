package com.example.makit.resetPassword.Service;

import com.example.makit.email.Exception.CustomException;
import com.example.makit.signup.Repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class ResetPasswordService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;

    public ResetPasswordService(JavaMailSender mailSender, UserRepository userRepository, StringRedisTemplate redisTemplate) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Async
    public void sendResetPasswordLink(String email, String link) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("비밀번호 재설정 링크");
            helper.setText("<p>비밀번호를 재설정하려면 다음 링크를 클릭하세요:</p>" +
                    "<a href='" + link + "'>비밀번호 재설정 링크</a>", true);
            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("비밀번호 재설정 이메일 전송 실패: " + e.getMessage());
        }
    }

    @Async
    public CompletableFuture<Boolean> checkEmailExists(String email) {
        return CompletableFuture.completedFuture(userRepository.findByEmail(email).isPresent());
    }

    public String generateAndStoreResetPasswordLink(String email) {
        String uniqueLink = "https://example.com/reset-password/" + UUID.randomUUID();
        String redisKey = "reset:" + email;
        redisTemplate.opsForValue().set(redisKey, uniqueLink, 30, TimeUnit.MINUTES);
        return uniqueLink;
    }

    public boolean validateResetLink(String email, String link) {
        String redisKey = "reset:" + email;
        String storedLink = redisTemplate.opsForValue().get(redisKey);
        return storedLink != null && storedLink.equals(link);
    }
}
