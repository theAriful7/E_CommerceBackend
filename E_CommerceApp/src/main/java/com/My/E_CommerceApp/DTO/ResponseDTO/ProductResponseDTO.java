package com.My.E_CommerceApp.DTO.ResponseDTO;

import com.My.E_CommerceApp.DTO.RequestDTO.FileDataDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.ProductSpecificationDTO;
import com.My.E_CommerceApp.Enum.ProductStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private List<FileDataDTO> images;
    private Double discount;
    private String brand;
    private String categoryName;
    private String subCategoryName;
    private ProductStatus status;
    private Long vendorId;
    private String vendorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProductSpecificationDTO> specifications;


}
