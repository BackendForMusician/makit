package com.example.makit.sex;

import com.example.makit.signup.Entity.GenreEntity;
import com.example.makit.signup.Entity.UserEntity;
import com.example.makit.signup.Entity.UserField;
import com.example.makit.signup.Entity.UserGenre;
import com.example.makit.signup.Repository.GenreRepository;
import com.example.makit.signup.Repository.UserFieldRepository;
import com.example.makit.signup.Repository.UserGenreRepository;
import com.example.makit.signup.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// git ignore <-- 합의되지 않은 픽스쳐가 존재한다
// 다른 팀원이 -> 이거 왜있음? 궁금해하고 찾게됨 -> 이거 시간낭비
@Component
@RequiredArgsConstructor
public class TestProfileSetting_By_ApplicationRunner implements ApplicationRunner {

    private static final String[] GENRE_SETTING = new String[]{
            "k-pop", "jazz"
    };

    private final UserRepository userRepository;
    private final UserFieldRepository userFieldRepository;
    private final GenreRepository genreRepository;
    private final UserGenreRepository userGenreRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        UserEntity user = new UserEntity();
        user.setEmail("sex");
        user.setPassword("sex!");
        user.setNickname("sex");
        user.setPhoneNumber("sex");
        userRepository.save(user);

        for (String genre : GENRE_SETTING) {
            GenreEntity genreEntity = new GenreEntity();
            genreEntity.setGenreName(genre);
            genreRepository.save(genreEntity);
        }
        
        UserField userField = new UserField();
        userField.setUser(user);

        UserGenre userGenre = new UserGenre();
        userGenre.setGenre(genreEntity);
        userGenre.setUser(user);
        userGenreRepository.save(userGenre);
    }

}
