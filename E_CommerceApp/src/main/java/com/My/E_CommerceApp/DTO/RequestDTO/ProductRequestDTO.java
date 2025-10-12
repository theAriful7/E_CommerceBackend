package com.My.E_CommerceApp.DTO.RequestDTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestDTO {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private Long categoryId;
}
