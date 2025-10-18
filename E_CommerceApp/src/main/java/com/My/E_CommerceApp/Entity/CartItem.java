package com.My.E_CommerceApp.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_items")
@EqualsAndHashCode(callSuper = true)
public class CartItem extends Base{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "price_per_item", nullable = false)
    private Double pricePerItem;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    // ✅ Automatically calculate total price
    public void calculateTotal() {
        if (pricePerItem != null && quantity != null) {
            this.totalPrice = pricePerItem * quantity;
        }
    }
}
