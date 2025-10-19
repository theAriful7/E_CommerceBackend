package com.My.E_CommerceApp.DTO.RequestDTO;

import lombok.Data;

@Data
public class AddressRequestDTO {
    private Long user_id;
    private String recipientName;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String phone;
}
