package com.My.E_CommerceApp.DTO.ResponseDTO;

import com.My.E_CommerceApp.Enum.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {

    private Long id;
    private String orderNumber;
    private Long userId;
    private Double totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private String shippingAddress;

    // Use separate OrderItemResponseDTO class, no nested class
    private List<OrderItemResponseDTO> items;
}
