package com.My.E_CommerceApp.DTO.ResponseDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;   // optional
    private LocalDateTime updatedAt;
}
