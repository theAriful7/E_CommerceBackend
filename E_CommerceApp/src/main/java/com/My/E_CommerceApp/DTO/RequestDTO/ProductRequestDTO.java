package com.My.E_CommerceApp.DTO.RequestDTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestDTO {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private Long categoryId;
    private Double discount;
    private String brand;
    // private Long vendorId; // optional, admin only
}
