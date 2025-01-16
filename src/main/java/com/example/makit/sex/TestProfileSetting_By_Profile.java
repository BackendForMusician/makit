package com.example.makit.sex;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;



/**
 * profile
 */
@Profile("!prod")
@Component
@RequiredArgsConstructor
class TestProfileSetting {

    // 접근하고자 하는 entity의 저장소 필드 세팅
    private final Repository repository;

    @PostConstruct
    void setUp() {

    }

}
