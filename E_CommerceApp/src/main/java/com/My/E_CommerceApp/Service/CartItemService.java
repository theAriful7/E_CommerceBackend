package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.CartItemRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.CartItemResponseDTO;
import com.My.E_CommerceApp.Entity.Cart;
import com.My.E_CommerceApp.Entity.CartItem;
import com.My.E_CommerceApp.Entity.Product;
import com.My.E_CommerceApp.Repository.AddressRepo;
import com.My.E_CommerceApp.Repository.CartItemRepo;
import com.My.E_CommerceApp.Repository.CartRepo;
import com.My.E_CommerceApp.Repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartItemService {

    private final CartItemRepo cartItemRepo;

    public CartItemService(CartItemRepo cartItemRepo) {
        this.cartItemRepo = cartItemRepo;
    }

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private ProductRepo productRepo;


    public CartItem toEntity(CartItemRequestDTO dto) {
        CartItem item = new CartItem();
        Cart cart = cartRepo.findById(dto.getCartId()).orElse(null);
        Product product = productRepo.findById(dto.getProductId()).orElse(null);

        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());

        if (product != null) {
            item.setTotalPrice(product.getPrice() * dto.getQuantity());

        } else {
            item.setTotalPrice(0.0);
        }
        return item;
    }


    public CartItemResponseDTO toDto(CartItem item) {
        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setId(item.getId());
        dto.setProductName(item.getProduct() != null ? item.getProduct().getName() : null);
        dto.setPricePerItem(item.getProduct() != null ? item.getProduct().getPrice() : 0.0);
        dto.setQuantity(item.getQuantity());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }


    // ✅ Create / Save new CartItem
    public CartItemResponseDTO save(CartItemRequestDTO dto) {
        CartItem entity = toEntity(dto);
        CartItem saved = cartItemRepo.save(entity);
        return toDto(saved);
    }

    // ✅ Get CartItem by ID
    public CartItemResponseDTO getById(Long id) {
        return cartItemRepo.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    // ✅ Get all CartItems
    public List<CartItemResponseDTO> getAll() {
        return cartItemRepo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Delete CartItem
    public void delete(Long id) {
        cartItemRepo.deleteById(id);
    }
}
