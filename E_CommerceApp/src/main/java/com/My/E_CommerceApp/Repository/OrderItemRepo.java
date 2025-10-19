package com.My.E_CommerceApp.Repository;

import com.My.E_CommerceApp.Entity.Address;
import com.My.E_CommerceApp.Entity.Order;
import com.My.E_CommerceApp.Entity.OrderItem;
import com.My.E_CommerceApp.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findByOrderAndProduct(Order order, Product product);
    List<OrderItem> findByOrder(Order order);

}
