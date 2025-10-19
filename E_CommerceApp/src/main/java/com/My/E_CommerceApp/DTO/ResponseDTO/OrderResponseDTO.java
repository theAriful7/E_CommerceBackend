package com.My.E_CommerceApp.DTO.ResponseDTO;

import com.My.E_CommerceApp.Entity.Address;
import com.My.E_CommerceApp.Enum.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {

    private Long id;
    private String orderNumber;
    private Long userId;



    private BigDecimal totalAmount; // Double → BigDecimal for money accuracy

    private OrderStatus status;
    private LocalDateTime orderDate;
    private Address shippingAddress;

    private List<OrderItemResponseDTO> items;
}
