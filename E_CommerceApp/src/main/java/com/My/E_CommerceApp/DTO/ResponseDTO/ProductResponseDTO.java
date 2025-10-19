package com.My.E_CommerceApp.DTO.ResponseDTO;

import com.My.E_CommerceApp.Enum.ProductStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private Double discount;   // optional
    private String brand;      // optional
    private String categoryName;
    private ProductStatus status;
    private Long vendorId;
    private String vendorName;
}
