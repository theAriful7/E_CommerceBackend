package com.My.E_CommerceApp.DTO.RequestDTO;

import com.My.E_CommerceApp.Entity.Address;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    private Long userId;
    private List<OrderItemRequestDTO> items; // productId + quantity
    private Address shippingAddress;
}
