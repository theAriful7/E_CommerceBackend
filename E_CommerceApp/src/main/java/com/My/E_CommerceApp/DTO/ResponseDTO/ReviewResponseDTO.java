package com.My.E_CommerceApp.DTO.ResponseDTO;

import lombok.Data;

@Data
public class ReviewResponseDTO {
    private Long id;
    private String userName;
    private String productName;
    private String comment;
    private Integer rating;
    private Boolean isActive;
}
