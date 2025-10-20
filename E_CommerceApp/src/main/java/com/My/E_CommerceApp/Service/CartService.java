package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.CartRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.CartItemResponseDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.CartResponseDTO;
import com.My.E_CommerceApp.Entity.Cart;
import com.My.E_CommerceApp.Entity.CartItem;
import com.My.E_CommerceApp.Entity.User;
import com.My.E_CommerceApp.Repository.CartRepo;
import com.My.E_CommerceApp.Repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {
    private final CartRepo cartRepo;
    private final UserRepo userRepo;
    private final CartItemService cartItemService;

    public CartService(CartRepo cartRepo, UserRepo userRepo, @Lazy CartItemService cartItemService) {
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
        this.cartItemService = cartItemService;
    }

    // ➤ Convert DTO → Entity
    public Cart toEntity(CartRequestDTO dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + dto.getUserId()));
        Cart cart = new Cart();
        cart.setUser(user);
        return cart;
    }

    // ➤ Convert Entity → DTO
    public CartResponseDTO toDto(Cart cart) {
        CartResponseDTO dto = new CartResponseDTO();
        dto.setId(cart.getId());
        dto.setUserName(cart.getUser().getFullName());
        dto.setTotalItems(cart.getItems().stream().mapToInt(CartItem::getQuantity).sum());
        dto.setTotalPrice(cart.getItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        // ✅ FIX: Convert CartItems to CartItemResponseDTOs
        List<CartItemResponseDTO> itemDTOs = cart.getItems().stream()
                .map(cartItemService::toDto)
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);

        return dto;
    }

    // ➤ Create Cart
    public CartResponseDTO createCart(CartRequestDTO dto) {
        Cart saved = cartRepo.save(toEntity(dto));
        return toDto(saved);
    }

    // ➤ Get Cart by ID
    public CartResponseDTO getCartById(Long id) {
        Cart cart = cartRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with ID: " + id));
        return toDto(cart);
    }

    // ➤ Get all Carts
    public List<CartResponseDTO> getAllCarts() {
        return cartRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    // ➤ Delete Cart
    public String deleteCart(Long id) {
        Cart cart = cartRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with ID: " + id));
        cartRepo.delete(cart);
        return "Cart deleted successfully.";
    }

    // ➤ Recalculate total price (optional helper for CartItems)
    public void recalculateTotal(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // BigDecimal safe addition

        cart.setTotalPrice(total);
        cartRepo.save(cart);
    }
}
