package com.example.makit.email.Service;

import com.example.makit.email.Entity.EmailEntity;
import com.example.makit.email.Repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;

     //인증번호 발송
    public void sendVerificationEmail(String email) throws MessagingException {
        // 이미 인증된 이메일인 경우
        EmailEntity existingEmail = emailRepository.findByEmail(email).orElse(null);
        if (existingEmail != null && existingEmail.isVerified()) {
            throw new RuntimeException("이미 가입된 이메일입니다.");  // 이메일 발송 안하고 에러 메시지 반환. 프론트에 넘겨줄 값
        }

        String verificationCode = generateVerificationCode();

        // 인증 정보 저장 및 업데이트
        saveOrUpdateVerificationCode(email, verificationCode);

        // 이메일 전송
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        //이메일 본문 양식
        helper.setTo(email);
        helper.setSubject("인증 코드");
        helper.setText("<p>귀하의 인증 코드는: <strong>" + verificationCode + "</strong></p>", true);

        mailSender.send(message);
    }



    // 인증번호 검증
    @Transactional
    public boolean validateVerificationCode(String email, String code) {
        EmailEntity emailEntity = emailRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("등록되지 않은 이메일입니다."));

        // 만료 여부 확인
        if (isCodeExpired(emailEntity)) {
            throw new IllegalStateException("시간이 초과되었습니다. 다시 입력해주세요.");  // 인증시간 초과
        }

        // 코드 일치 여부 확인
        if (emailEntity.getCode().equals(code)) {
            emailEntity.setVerified(true);
            emailEntity.setVerifiedAt(LocalDateTime.now());
            emailRepository.save(emailEntity);
            return true;
        }
        return false;
    }
    //인증 상태 확인

    public boolean checkVerificationStatus(String email) {
        EmailEntity emailEntity = emailRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("이메일을 찾을 수 없습니다."));

        return emailEntity.isVerified();
    }



    // 인증번호 재전송
    public void resendVerificationCode(String email) throws MessagingException {
        EmailEntity emailEntity = emailRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("등록되지 않은 이메일입니다."));

        // 이미 인증된 이메일은 재전송하지 않도록. send 부분에서 넘어갈 수 없도록 해둠.
        if (emailEntity.isVerified()) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }

        String newCode = generateVerificationCode();

        // 인증 정보 업데이트
        saveOrUpdateVerificationCode(email, newCode);

        // 이메일 재전송
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        //이메일 본문 내용 양식
        helper.setTo(email);
        helper.setSubject("새로운 인증 코드");
        helper.setText("<p>귀하의 새로운 인증 코드는: <strong>" + newCode + "</strong></p>", true);

        mailSender.send(message);
    }

    // 랜덤 인증번호 생성
    private String generateVerificationCode() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000)); // 6자리
    }

    // 인증정보 저장 및 업데이트
    private void saveOrUpdateVerificationCode(String email, String code) {
        EmailEntity emailEntity = emailRepository.findByEmail(email)
                .orElse(new EmailEntity(null, email, code, LocalDateTime.now(), false, null));

        emailEntity.setCode(code);
        emailEntity.setSentAt(LocalDateTime.now());
        emailEntity.setVerified(false); // 초기화
        emailEntity.setVerifiedAt(null); // 인증 성공 시간 초기화
        emailRepository.save(emailEntity);
    }

    // 인증번호 만료 확인 (기능명세서에 따라 3분 기준)
    private boolean isCodeExpired(EmailEntity emailEntity) {
        return Duration.between(emailEntity.getSentAt(), LocalDateTime.now()).toMinutes() > 3;
    }
}
