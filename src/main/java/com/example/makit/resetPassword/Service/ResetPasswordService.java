package com.example.makit.resetPassword.Service;

import com.example.makit.signup.DTO.SignupRequestDTO;
import com.example.makit.signup.Service.PasswordValidationResponse;
import com.example.makit.signup.Validator.PasswordValidator;
import com.example.makit.signup.Repository.UserRepository;
import com.example.makit.signup.Entity.UserEntity;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class ResetPasswordService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

    public PasswordValidationResponse validateAndUpdatePassword(String email, SignupRequestDTO request) {
        PasswordValidationResponse response = new PasswordValidationResponse();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        if (!PasswordValidator.isValidPassword(password)) {
            response.setIsPasswordValid(false);
            response.setPasswordErrorMessage("비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자를 포함해야 합니다.");
            return response;
        }

        if (!password.equals(confirmPassword)) {
            response.setIsPasswordMatch(false);
            response.setPasswordMatchErrorMessage("비밀번호가 일치하지 않습니다.");
            return response;
        }

        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 이메일입니다."));
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        response.setIsPasswordValid(true);
        response.setIsPasswordMatch(true);

        return response;
    }
}
