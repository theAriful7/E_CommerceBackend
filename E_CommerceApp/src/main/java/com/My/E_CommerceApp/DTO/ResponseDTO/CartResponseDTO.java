package com.My.E_CommerceApp.DTO.ResponseDTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponseDTO {
    private Long id;
    private String userName;
    private Integer totalItems;
    private BigDecimal totalPrice;
    private List<CartItemResponseDTO> items;

}
