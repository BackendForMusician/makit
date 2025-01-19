package com.example.makit.signup.Service;

import com.example.makit.signup.Repository.FieldRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldService {
    private final FieldRepository fieldRepository;

    public FieldService(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    // 모든 분야의 이름만 반환
    public List<String> getAllFieldNames() {
        return fieldRepository.findAllBy();
    }
}
