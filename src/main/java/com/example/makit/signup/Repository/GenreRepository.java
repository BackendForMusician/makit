package com.example.makit.signup.Repository;

import com.example.makit.signup.Entity.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GenreRepository extends JpaRepository<GenreEntity, Long> {

    @Query("SELECT g.genreName FROM GenreEntity g")
    List<String> findAllBy();

    // 이름으로 장르 조회
    Optional<GenreEntity> findByGenreName(String genreName);
}
