package com.My.E_CommerceApp.DTO.ResponseDTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponseDTO {
    private Long productId;
    private String productName; // user friendly field

    private Integer quantity;

    private BigDecimal price;       // একক দাম
    private BigDecimal totalPrice;
}
