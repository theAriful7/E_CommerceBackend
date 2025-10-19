package com.My.E_CommerceApp.DTO.RequestDTO;

import com.My.E_CommerceApp.Entity.Address;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderRequestDTO {
    private Long userId;
    private Long shippingAddressId;
    private List<OrderItemRequestDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
