package com.My.E_CommerceApp.DTO.ResponseDTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponseDTO {
    private Long id;
    private String productName;
    private BigDecimal pricePerItem;
    private Integer quantity;
    private BigDecimal totalPrice;
}
