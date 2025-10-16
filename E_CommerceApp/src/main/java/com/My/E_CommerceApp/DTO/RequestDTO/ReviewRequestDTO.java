package com.My.E_CommerceApp.DTO.RequestDTO;

import lombok.Data;

@Data
public class ReviewRequestDTO {
    private Long userId;
    private Long productId;
    private String comment;
    private Integer rating;
}
