package com.My.E_CommerceApp.Controller;

import com.My.E_CommerceApp.DTO.RequestDTO.CartItemRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.CartItemResponseDTO;
import com.My.E_CommerceApp.Service.AddressService;
import com.My.E_CommerceApp.Service.CartItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart_items")
@CrossOrigin(origins = "http://localhost:4200")
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PostMapping
    public ResponseEntity<CartItemResponseDTO> createCartItem(@RequestBody CartItemRequestDTO dto) {
        return ResponseEntity.ok(cartItemService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartItemResponseDTO> getCartItemById(@PathVariable Long id) {
        return ResponseEntity.ok(cartItemService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponseDTO>> getAllCartItems() {
        return ResponseEntity.ok(cartItemService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCartItem(@PathVariable Long id) {
        return ResponseEntity.ok(cartItemService.delete(id));
    }
}
