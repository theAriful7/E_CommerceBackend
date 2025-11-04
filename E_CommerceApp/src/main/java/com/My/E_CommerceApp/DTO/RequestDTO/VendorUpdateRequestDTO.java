package com.My.E_CommerceApp.DTO.RequestDTO;

import lombok.Data;

@Data
public class VendorUpdateRequestDTO {
    private String shopName;
    private String shopDescription;
    private String shopLogo;
    private String businessEmail;
    private String businessPhone;
    private String taxNumber;
    private String bankAccountDetails;
}
