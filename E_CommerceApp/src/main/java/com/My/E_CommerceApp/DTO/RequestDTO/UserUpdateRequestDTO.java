package com.My.E_CommerceApp.DTO.RequestDTO;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.List;

@Data
public class UserUpdateRequestDTO {
    private String fullName;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;
    private String profileImage;
    private String bio;

    // Vendor (seller) fields — optional
    private String shopName;
    private String shopDescription;
    private String shopLogo;

    private List<AddressRequestDTO> addresses;
}
