package com.My.E_CommerceApp.DTO.ResponseDTO;

import com.My.E_CommerceApp.Enum.VendorStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VendorResponseDTO {
    private Long id;
    private String shopName;
    private String shopDescription;
    private String shopLogo;
    private Double averageRating;
    private String businessEmail;
    private String businessPhone;
    private String taxNumber;
    private VendorStatus vendorStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Basic user info
    private UserBasicResponseDTO user;
}