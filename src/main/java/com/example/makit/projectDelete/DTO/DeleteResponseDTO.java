package com.example.makit.projectDelete.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeleteResponseDTO {
    private boolean success;
    private String message;
}
