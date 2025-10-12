package com.My.E_CommerceApp.DTO.ResponseDTO;

import com.My.E_CommerceApp.Enum.Role;
import lombok.Data;

import java.util.List;

@Data
public class AddressResponseDTO {

    private Long id;
    private String street;
    private String city;
    private String country;
    private String postalCode;
}
