package com.My.E_CommerceApp.DTO.ResponseDTO;

import com.My.E_CommerceApp.Enum.VendorStatus;
import lombok.Data;

@Data
public class VendorBasicResponseDTO {
    private Long id;
    private String shopName;
    private String shopLogo;
    private Double averageRating;
    private VendorStatus vendorStatus;
}
