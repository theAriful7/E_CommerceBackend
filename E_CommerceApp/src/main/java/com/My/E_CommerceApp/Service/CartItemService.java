package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.CartItemRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.CartItemResponseDTO;
import com.My.E_CommerceApp.Entity.Cart;
import com.My.E_CommerceApp.Entity.CartItem;
import com.My.E_CommerceApp.Entity.Product;
import com.My.E_CommerceApp.Entity.User;
import com.My.E_CommerceApp.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepo cartItemRepo;
    private final CartRepo cartRepo;
    private final ProductRepo productRepo;
    @Lazy
    private final CartService cartService;

    // ➤ Convert DTO → Entity
    public CartItem toEntity(CartItemRequestDTO dto) {
        Cart cart = cartRepo.findById(dto.getCartId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        item.setPricePerItem(product.getPrice());

        // ✅ ONLY THIS: Set the user from the cart
        item.setUser(cart.getUser());

        // Calculate total price
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
        item.setTotalPrice(totalPrice);

        return item;
    }

    // ➤ Convert Entity → DTO
    public CartItemResponseDTO toDto(CartItem item) {
        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setId(item.getId());
        dto.setProductName(item.getProduct().getName());
        dto.setPricePerItem(item.getPricePerItem());
        dto.setQuantity(item.getQuantity());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }

    // ➤ Save CartItem
    public CartItemResponseDTO save(CartItemRequestDTO dto) {
        CartItem item = toEntity(dto);
        CartItem saved = cartItemRepo.save(item);
        cartService.recalculateTotal(item.getCart()); // recalc total
        return toDto(saved);
    }

    // ➤ Get CartItem by ID
    public CartItemResponseDTO getById(Long id) {
        CartItem item = cartItemRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found"));
        return toDto(item);
    }

    // ➤ Get all CartItems
    public List<CartItemResponseDTO> getAll() {
        return cartItemRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    // ➤ Delete CartItem
    public String delete(Long id) {
        CartItem item = cartItemRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found"));
        Cart cart = item.getCart();
        cartItemRepo.delete(item);
        cartService.recalculateTotal(cart);
        return "Cart item deleted successfully!";
    }
}
