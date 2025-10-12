package com.My.E_CommerceApp.DTO.RequestDTO;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Long orderId;          // যে Order এর জন্য payment হবে তার ID
    private Long amount;     // মোট টাকা (Order এর মোট amount)
    private String paymentMethod;
}
