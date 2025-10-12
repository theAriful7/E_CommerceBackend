package com.My.E_CommerceApp.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
@EqualsAndHashCode(callSuper = true)
public class Payment extends Base{

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Long amount;

    private String paymentMethod;

    private String paymentStatus = "PENDING";

    private LocalDateTime paymentDate = LocalDateTime.now();

}
