package com.example.makit.signup.Repository;

import com.example.makit.signup.Entity.FieldEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FieldRepository extends JpaRepository<FieldEntity, Long> {

    @Query("SELECT f.fieldName FROM FieldEntity f")
    List<String> findAllBy();

    // 이름으로 분야 조회
    Optional<FieldEntity> findByFieldName(String fieldName);
}
