package com.example.makit.signup.Service;

import com.example.makit.signup.Repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    // 모든 장르의 이름만 반환 (Repository에서 직접 이름만 조회)
    public List<String> getAllGenreNames() {
        return genreRepository.findAllBy();
    }
}