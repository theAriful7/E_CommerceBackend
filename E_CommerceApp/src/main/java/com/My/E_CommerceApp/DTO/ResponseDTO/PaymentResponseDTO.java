package com.My.E_CommerceApp.DTO.ResponseDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {

        private Long id;                  // Payment এর unique ID
        private Long orderId;
        private Long amount;
        private String paymentMethod;
        private String paymentStatus;
        private LocalDateTime paymentDate;
}
