package com.My.E_CommerceApp.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carts")
@EqualsAndHashCode(callSuper = true)
public class Cart extends Base{

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @Column(name = "total_price", nullable = false)
    private Double totalPrice = 0.0;


    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
        recalculateTotal();
    }


    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
        recalculateTotal();
    }


    public void recalculateTotal() {
        this.totalPrice = items.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }


    public int getTotalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
