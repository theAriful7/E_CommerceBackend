package com.My.E_CommerceApp.Repository;

import com.My.E_CommerceApp.Entity.Address;
import com.My.E_CommerceApp.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
}
