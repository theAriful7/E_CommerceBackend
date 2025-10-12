package com.My.E_CommerceApp.DTO.RequestDTO;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    private Long userId;
    private List<OrderItemRequestDTO> items; // productId + quantity
    private String shippingAddress;
}
