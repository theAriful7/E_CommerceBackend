package com.My.E_CommerceApp.DTO.RequestDTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartRequestDTO {
    private Long userId;
    private BigDecimal totalPrice = BigDecimal.ZERO;
}
