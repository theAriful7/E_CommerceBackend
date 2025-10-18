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
@CrossOrigin(origins = "http://localhost:4200")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<CartResponseDTO> createCart(@RequestBody CartRequestDTO dto) {
        return ResponseEntity.ok(cartService.createCart(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartResponseDTO> getCartById(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.getCartById(id));
    }

    @GetMapping
    public ResponseEntity<List<CartResponseDTO>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCart(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.deleteCart(id));
    }

}
