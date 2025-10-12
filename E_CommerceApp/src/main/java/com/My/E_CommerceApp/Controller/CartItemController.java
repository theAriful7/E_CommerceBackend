package com.My.E_CommerceApp.Controller;

import com.My.E_CommerceApp.DTO.RequestDTO.CartItemRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.CartItemResponseDTO;
import com.My.E_CommerceApp.Service.AddressService;
import com.My.E_CommerceApp.Service.CartItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart_items")
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    // ✅ Create CartItem
    @PostMapping
    public CartItemResponseDTO createCartItem(@RequestBody CartItemRequestDTO dto) {
        return cartItemService.save(dto);
    }

    // ✅ Get by ID
    @GetMapping("/{id}")
    public CartItemResponseDTO getCartItemById(@PathVariable Long id) {
        return cartItemService.getById(id);
    }

    // ✅ Get all
    @GetMapping
    public List<CartItemResponseDTO> getAllCartItems() {
        return cartItemService.getAll();
    }

    // ✅ Delete
    @DeleteMapping("/{id}")
    public String deleteCartItem(@PathVariable Long id) {
        cartItemService.delete(id);
        return "Cart item deleted successfully!";
    }
}
