package com.My.E_CommerceApp.DTO.ResponseDTO;

import lombok.Data;

@Data
public class UserBasicResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String profileImage;
}
