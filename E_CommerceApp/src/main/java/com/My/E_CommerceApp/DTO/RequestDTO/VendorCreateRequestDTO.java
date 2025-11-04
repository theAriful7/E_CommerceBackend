package com.My.E_CommerceApp.DTO.RequestDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VendorCreateRequestDTO {
    // User fields
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    @NotBlank(message = "Password is required")
    private String password;

    private String profileImage;
    private String gender;
    private String bio;

    // Vendor fields
    @NotBlank(message = "Shop name is required")
    private String shopName;

    private String shopDescription;
    private String shopLogo;
    private String businessEmail;
    private String businessPhone;
    private String taxNumber;
}
