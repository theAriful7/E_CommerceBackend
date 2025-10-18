package com.My.E_CommerceApp.Repository;

import com.My.E_CommerceApp.Entity.Address;
import com.My.E_CommerceApp.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);
}
