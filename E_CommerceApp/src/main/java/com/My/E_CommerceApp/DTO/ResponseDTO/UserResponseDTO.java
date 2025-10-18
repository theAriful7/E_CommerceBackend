package com.My.E_CommerceApp.DTO.ResponseDTO;

import com.My.E_CommerceApp.Enum.AccountStatus;
import com.My.E_CommerceApp.Enum.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private Boolean isActive;
    private AccountStatus accountStatus;

    private String profileImage;
//    private String bio;
//    private String gender;

    // Vendor info (optional)
//    private String shopName;
//    private String shopDescription;
//    private String shopLogo;
//    private Double averageRating;

    // Audit info
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
//    private String updatedBy;

    private List<AddressResponseDTO> addresses;
}
