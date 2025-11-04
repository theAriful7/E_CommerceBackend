package com.My.E_CommerceApp.DTO.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VendorRequestDTO {
    @NotBlank(message = "Shop name is required")
    private String shopName;

    private String shopDescription;
    private String shopLogo;
    private String businessEmail;
    private String businessPhone;
    private String taxNumber;
    private String bankAccountDetails;
}
