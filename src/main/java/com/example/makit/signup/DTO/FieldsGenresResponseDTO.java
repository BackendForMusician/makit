package com.example.makit.signup.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FieldsGenresResponseDTO {
    private List<String> fields;  // 분야 이름 리스트
    private List<String> genres;  // 장르 이름 리스트
}
