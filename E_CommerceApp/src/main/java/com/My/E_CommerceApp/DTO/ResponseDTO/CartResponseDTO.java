package com.My.E_CommerceApp.DTO.ResponseDTO;

import lombok.Data;

import java.util.List;

@Data
public class CartResponseDTO {
    private Long id;
    private String userName;
    private Integer totalItems;
    private Double totalPrice;
    private List<CartItemResponseDTO> items;

}
