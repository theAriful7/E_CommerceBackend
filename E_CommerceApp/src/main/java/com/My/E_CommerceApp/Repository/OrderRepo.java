package com.My.E_CommerceApp.Repository;

import com.My.E_CommerceApp.Entity.Address;
import com.My.E_CommerceApp.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
}
