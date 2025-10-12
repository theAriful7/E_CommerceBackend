package com.My.E_CommerceApp.DTO.RequestDTO;

import lombok.Data;

@Data
public class CartItemRequestDTO {
    private Long cartId;
    private Long productId;
    private Integer quantity;
}
