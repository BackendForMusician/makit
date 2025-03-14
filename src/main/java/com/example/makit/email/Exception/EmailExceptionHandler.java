package com.example.makit.email.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class EmailExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // 필드 오류 메시지 처리
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    // CustomException 처리 (비즈니스 로직 예외)
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, String>> handleBusinessExceptions(CustomException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());  // CustomException에서 정의한 message
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
