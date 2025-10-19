package com.My.E_CommerceApp.DTO.ResponseDTO;

import com.My.E_CommerceApp.Enum.Role;
import lombok.Data;

import java.util.List;

@Data
public class AddressResponseDTO {

    private String recipientName;
    private Long id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String phone;
}
