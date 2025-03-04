package com.example.makit.setup;

import com.example.makit.exception.GenreNotFoundException;
import com.example.makit.signup.DTO.TermsAgreement;
import com.example.makit.signup.Entity.*;
import com.example.makit.signup.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class TestProfileSetting_By_ApplicationRunner implements ApplicationRunner {

    private static final String[] GENRE_SETTING = new String[]{
            "팝", "힙합", "록", "재즈", "인디", "R&B", "클래식", "트로트", "컨트리", "일렉트로닉", "발라드", "그 외"
    };

    private static final String[] FIELD_SETTING = new String[]{
            "작사", "믹싱", "비트메이커", "프로듀서", "작곡/편곡", "마스터링", "앨범아트", "세션", "보컬", "영상", "그 외"
    };

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final FieldRepository fieldRepository;
    private final UserFieldRepository userFieldRepository;
    private final GenreRepository genreRepository;
    private final UserGenreRepository userGenreRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        for (String genre : GENRE_SETTING) {
            GenreEntity genreEntity = new GenreEntity();
            genreEntity.setGenreName(genre);
            genreRepository.save(genreEntity);
        }
        for (String field : FIELD_SETTING) {
            FieldEntity fieldEntity = new FieldEntity();
            fieldEntity.setFieldName(field);
            fieldRepository.save(fieldEntity);
        }

        UserEntity user = new UserEntity();
        user.setEmail("picetea44@gmail.com");
        user.setPassword(passwordEncoder.encode("test1995@"));
        user.setNickname("테스트유저1");
        user.setPhoneNumber("010-1234-5678");
        user.setUserType("USER");
        TermsAgreement termsAgreement = new TermsAgreement(true,true,true,true,true);
        user.setTermsAgreement(termsAgreement);
        userRepository.save(user);


        FieldEntity field = fieldRepository.findByFieldName("작사").orElseThrow(() -> new RuntimeException("Field not found"));

        UserField userField = new UserField();
        userField.setUser(user);
        userField.setField(field);
        userFieldRepository.save(userField);

        GenreEntity genre = genreRepository.findByGenreName("팝").orElseThrow(() -> new GenreNotFoundException("팝"));

        UserGenre userGenre = new UserGenre();
        userGenre.setGenre(genre);
        userGenre.setUser(user);
        userGenreRepository.save(userGenre);
    }

}
