package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.CartRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.CartResponseDTO;
import com.My.E_CommerceApp.Entity.Cart;
import com.My.E_CommerceApp.Entity.User;
import com.My.E_CommerceApp.Repository.CartRepo;
import com.My.E_CommerceApp.Repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepo cartRepo;
    private final UserRepo userRepo;

    public CartService(CartRepo cartRepo, UserRepo userRepo) {
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
    }


    public Cart toEntity(CartRequestDTO dto) {
        Cart cart = new Cart();
        User user = userRepo.findById(dto.getUserId()).orElse(null);
        cart.setUser(user);
        cart.setTotalPrice(0.0);
        return cart;
    }


    public CartResponseDTO toDto(Cart cart) {
        CartResponseDTO dto = new CartResponseDTO();
        dto.setId(cart.getId());
        dto.setUserName(cart.getUser() != null ? cart.getUser().getFullName() : null);
        dto.setTotalItems(cart.getItems() != null ? cart.getItems().size() : 0);
        dto.setTotalPrice(cart.getTotalPrice());
        return dto;
    }

    // ➕ Create new Cart
    public CartResponseDTO createCart(CartRequestDTO dto) {
        Cart saved = cartRepo.save(toEntity(dto));
        return toDto(saved);
    }

    // 🔍 Get Cart by ID
    public CartResponseDTO getCartById(Long id) {
        Cart cart = cartRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + id));
        return toDto(cart);
    }

    // 📋 Get all Carts
    public List<CartResponseDTO> getAllCarts() {
        return cartRepo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✏️ Update Cart (e.g. update total price)
    public CartResponseDTO updateCart(Long id, Double totalPrice) {
        Cart existing = cartRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + id));
        existing.setTotalPrice(totalPrice);
        Cart updated = cartRepo.save(existing);
        return toDto(updated);
    }

    // ❌ Delete Cart
    public String deleteCart(Long id) {
        Cart existing = cartRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + id));
        cartRepo.delete(existing);
        return "Cart deleted successfully.";
    }
}
