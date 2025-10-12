package com.My.E_CommerceApp.DTO.RequestDTO;

import lombok.Data;

@Data
public class OrderItemRequestDTO {

    private Long order_id;

    private Long productId;

    private Integer quantity;
}
