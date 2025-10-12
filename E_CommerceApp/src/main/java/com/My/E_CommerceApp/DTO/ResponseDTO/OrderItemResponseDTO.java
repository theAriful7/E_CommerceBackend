package com.My.E_CommerceApp.DTO.ResponseDTO;

import lombok.Data;

@Data
public class OrderItemResponseDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double pricePerItem;
    private Double totalPrice;
}
