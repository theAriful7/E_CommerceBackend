package com.My.E_CommerceApp.DTO.RequestDTO;

import com.My.E_CommerceApp.Enum.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserRequestDTO {

    private String fullName;
    private String email;
    private String phone;
    private String password;
    private Role role;
    private List<AddressRequestDTO> addresses; // একাধিক ঠিকানা নিতে পারবে
}
