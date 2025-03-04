package com.example.makit.signup.Converter;

import com.example.makit.signup.DTO.TermsAgreement;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TermsAgreementConverter implements AttributeConverter<TermsAgreement, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(TermsAgreement attribute) {
        if (attribute == null) return null;
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            // 필요한 예외 처리
            throw new RuntimeException("Could not write JSON: " + e.getMessage());
        }
    }

    @Override
    public TermsAgreement convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        try {
            return objectMapper.readValue(dbData, TermsAgreement.class);
        } catch (JsonProcessingException e) {
            // 필요한 예외 처리
            throw new RuntimeException("Could not read JSON: " + e.getMessage());
        }
    }
}
