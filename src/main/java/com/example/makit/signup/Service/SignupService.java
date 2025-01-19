package com.example.makit.signup.Service;

import com.example.makit.signup.DTO.SignupRequestDTO;
import com.example.makit.signup.Entity.*;
import com.example.makit.signup.Repository.*;
import com.example.makit.signup.Validator.NicknameValidator;
import com.example.makit.signup.Validator.PasswordValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SignupService {

    //password part

    // 비밀번호 암호화를 위한 BCryptPasswordEncoder 객체 생성
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // HttpSession 객체 주입 받기 (세션 관리)
    private final HttpSession session;

    // 엔티티별 리포지토리
    private final UserRepository userRepository;
    private final FieldRepository fieldRepository;
    private final GenreRepository genreRepository;
    private final UserFieldRepository userFieldRepository;
    private final UserGenreRepository userGenreRepository;

    // 생성자 주입을 통해 HttpSession 객체를 받을 수 있도록 설정
    public SignupService(HttpSession session, UserRepository userRepository, FieldRepository fieldRepository,
                         GenreRepository genreRepository, UserFieldRepository userFieldRepository,
                         UserGenreRepository userGenreRepository) {
        this.session = session;
        this.userRepository = userRepository;
        this.fieldRepository = fieldRepository;
        this.genreRepository = genreRepository;
        this.userFieldRepository = userFieldRepository;
        this.userGenreRepository = userGenreRepository;
    }

    // 비밀번호 유효성 검사 및 비밀번호 해싱
    @Transactional
    public PasswordValidationResponse validatePasswords(SignupRequestDTO request) {
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        PasswordValidationResponse response = new PasswordValidationResponse();

        // 비밀번호 형식 검증. figma에 따른 메세지 반환
        if (!PasswordValidator.isValidPassword(password)) {
            response.setIsPasswordValid(false); // 비밀번호 유효성 실패
            response.setPasswordErrorMessage("비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자를 포함해야 합니다.");
        } else {
            response.setIsPasswordValid(true); // 비밀번호 유효성 성공
        }

        // 비밀번호 일치 여부 검증
        if (!password.equals(confirmPassword)) {
            response.setIsPasswordMatch(false); // 비밀번호 불일치
            response.setPasswordMatchErrorMessage("다시 입력해주세요.");
        } else {
            response.setIsPasswordMatch(true); // 비밀번호 일치
        }

        // 비밀번호가 유효하고, 비밀번호 확인이 일치하면 비밀번호 해싱 및 세션에 저장
        if (response.isPasswordValid() && response.isPasswordMatch()) {
            // 비밀번호 해싱
            String hashedPassword = passwordEncoder.encode(password);

            // 세션에 해싱된 비밀번호 저장 (임시 저장)
            session.setAttribute("hashedPassword", hashedPassword);

            // 위에서 isPasswordValid, isPasswordMatch가 모두 true일 때 닉네임 페이지로 넘어갈 수 있다는 의미
            // endpoint로 redirection 및 session의 password값 저장 유무를 통해 페이지 이동 가능
            response.setRedirectToNicknamePage(true);
        }

        return response;
    }


    //nickname part

    public boolean validateAndSaveNickname(String nickname) {
        if (NicknameValidator.isValidNickname(nickname)) {
            // 유효하다면 세션에 저장
            session.setAttribute("nickname", nickname);
            return true;
        }
        return false;
    }


    //phone number part
    // 전화번호 유효성 검사 및 세션 저장
    public boolean validateAndSavePhoneNumber(String phoneNumber) {
        // 유효성 검사: 숫자만 허용 + 길이 제한 (10~11자리)
        if (phoneNumber != null && phoneNumber.matches("\\d{10,11}")) {
            // 유효하다면 세션에 저장
            session.setAttribute("phoneNumber", phoneNumber);
            return true;
        }
        return false;
    }

    // 회원가입 완료 메서드
    @Transactional
    public boolean completeSignup(List<String> selectedFields, List<String> selectedGenres) {
        // 1. 세션에서 사용자 정보 가져오기
        String email = (String) session.getAttribute("email");
        String hashedPassword = (String) session.getAttribute("hashedPassword");
        String nickname = (String) session.getAttribute("nickname");
        String phoneNumber = (String) session.getAttribute("phoneNumber");

        // 2. 세션에 저장된 정보가 모두 존재하는지 확인
        if (email == null || hashedPassword == null || nickname == null || phoneNumber == null) {
            throw new IllegalStateException("세션에 저장된 회원 정보가 부족합니다.");
        }

        // 3. 사용자 엔티티 생성 및 저장
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setNickname(nickname);
        user.setPhoneNumber(phoneNumber);
        userRepository.save(user);

        // 4. 분야 정보 저장 (이름으로 조회)
        for (String fieldName : selectedFields) {
            FieldEntity field = fieldRepository.findByFieldName(fieldName)
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 분야 이름: " + fieldName));
            UserField userField = new UserField();
            userField.setUser(user);
            userField.setField(field);
            userFieldRepository.save(userField);
        }

        // 5. 장르 정보 저장 (이름으로 조회)
        for (String genreName : selectedGenres) {
            GenreEntity genre = genreRepository.findByGenreName(genreName)
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 장르 이름: " + genreName));
            UserGenre userGenre = new UserGenre();
            userGenre.setUser(user);
            userGenre.setGenre(genre);
            userGenreRepository.save(userGenre);
        }

        return true; // 회원가입 완료
    }
}
