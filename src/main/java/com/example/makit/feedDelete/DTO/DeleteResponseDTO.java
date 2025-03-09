package com.example.makit.feedDelete.DTO;

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
