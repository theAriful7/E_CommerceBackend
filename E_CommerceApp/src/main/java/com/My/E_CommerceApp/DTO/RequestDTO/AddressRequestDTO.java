package com.My.E_CommerceApp.DTO.RequestDTO;

import lombok.Data;

@Data
public class AddressRequestDTO {

    private String street;
    private String city;
    private String country;
    private String postalCode;
}
