package com.My.E_CommerceApp.Controller;

import com.My.E_CommerceApp.DTO.RequestDTO.CartRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.CartResponseDTO;
import com.My.E_CommerceApp.Service.AddressService;
import com.My.E_CommerceApp.Service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // ➕ Create a new Cart
    @PostMapping
    public ResponseEntity<CartResponseDTO> createCart(@RequestBody CartRequestDTO dto) {
        return ResponseEntity.ok(cartService.createCart(dto));
    }

    // 🔍 Get cart by ID
    @GetMapping("/{id}")
    public ResponseEntity<CartResponseDTO> getCartById(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.getCartById(id));
    }

    // 📋 Get all carts
    @GetMapping
    public ResponseEntity<List<CartResponseDTO>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }

    // ✏️ Update cart total price
    @PutMapping("/{id}")
    public ResponseEntity<CartResponseDTO> updateCart(@PathVariable Long id,
                                                      @RequestParam Double totalPrice) {
        return ResponseEntity.ok(cartService.updateCart(id, totalPrice));
    }

    // ❌ Delete cart
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCart(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.deleteCart(id));
    }
}
