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
    private String profileImage;
    private String gender;
    private String bio;
    private Boolean isActive;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AddressResponseDTO> addresses;

    // Vendor info (if user is a vendor)
    private VendorBasicResponseDTO vendor;
}
