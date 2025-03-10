package com.example.makit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FieldNotFoundException extends RuntimeException {
    public FieldNotFoundException(String name) {super("Field not found: " + name);}
}
