package com.My.E_CommerceApp.DTO.ResponseDTO;

import com.My.E_CommerceApp.Enum.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserResponseDTO {

    private Long id;               // Database ID
    private String fullName;       // ব্যবহারকারীর নাম
    private String email;          // ইমেইল
    private String phone;          // ফোন নাম্বার
    private Role role;             // ভূমিকা (Customer / Admin)
    private Boolean isActive;      // Account status
    private List<AddressResponseDTO> addresses;
}
