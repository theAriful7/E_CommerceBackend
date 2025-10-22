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

    // ✅ CREATE: Add item to cart
    public CartItemResponseDTO addItemToCart(CartItemRequestDTO requestDTO) {
        Cart cart = cartRepo.findById(requestDTO.getCartId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with ID: " + requestDTO.getCartId()));

        Product product = productRepo.findById(requestDTO.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + requestDTO.getProductId()));

        // Check if item already exists in cart
        CartItem existingItem = cartItemRepo.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (existingItem != null) {
            // Update quantity if item exists
            existingItem.setQuantity(existingItem.getQuantity() + requestDTO.getQuantity());
            CartItem updated = cartItemRepo.save(existingItem);
            return toDto(updated);
        } else {
            // Create new cart item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(requestDTO.getQuantity());
            newItem.setPricePerItem(product.getPrice());

            CartItem saved = cartItemRepo.save(newItem);
            return toDto(saved);
        }
    }

    // ✅ READ: Get cart item by ID
    public CartItemResponseDTO getCartItemById(Long id) {
        CartItem cartItem = cartItemRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with ID: " + id));
        return toDto(cartItem);
    }

    // ✅ READ: Get all cart items for a specific cart
    public List<CartItemResponseDTO> getCartItemsByCartId(Long cartId) {
        List<CartItem> cartItems = cartItemRepo.findByCartId(cartId);
        return cartItems.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ READ: Get all cart items
    public List<CartItemResponseDTO> getAllCartItems() {
        List<CartItem> cartItems = cartItemRepo.findAll();
        return cartItems.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ UPDATE: Update cart item quantity
    public CartItemResponseDTO updateCartItemQuantity(Long id, Integer newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        CartItem cartItem = cartItemRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with ID: " + id));

        cartItem.setQuantity(newQuantity);
        CartItem updated = cartItemRepo.save(cartItem);
        return toDto(updated);
    }

    // ✅ UPDATE: Update cart item (full update)
    public CartItemResponseDTO updateCartItem(Long id, CartItemRequestDTO requestDTO) {
        CartItem cartItem = cartItemRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with ID: " + id));

        if (requestDTO.getProductId() != null) {
            Product product = productRepo.findById(requestDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + requestDTO.getProductId()));
            cartItem.setProduct(product);
            cartItem.setPricePerItem(product.getPrice()); // Update price when product changes
        }

        if (requestDTO.getQuantity() != null) {
            if (requestDTO.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }
            cartItem.setQuantity(requestDTO.getQuantity());
        }

        CartItem updated = cartItemRepo.save(cartItem);
        return toDto(updated);
    }

    // ✅ DELETE: Remove cart item by ID
    public String deleteCartItem(Long id) {
        CartItem cartItem = cartItemRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with ID: " + id));

        cartItemRepo.delete(cartItem);
        return "Cart item deleted successfully!";
    }

    // ✅ DELETE: Remove cart item by cart and product
    public String deleteCartItemByCartAndProduct(Long cartId, Long productId) {
        CartItem cartItem = cartItemRepo.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found for cart ID: " + cartId + " and product ID: " + productId));

        cartItemRepo.delete(cartItem);
        return "Cart item deleted successfully!";
    }

    // ✅ DELETE: Remove all items from cart
    public String clearCartItems(Long cartId) {
        List<CartItem> cartItems = cartItemRepo.findByCartId(cartId);
        if (!cartItems.isEmpty()) {
            cartItemRepo.deleteAll(cartItems);
            return "All cart items cleared successfully!";
        }
        return "Cart is already empty!";
    }

    // ✅ COUNT: Get total items count in cart
    public Integer getCartItemsCount(Long cartId) {
        List<CartItem> cartItems = cartItemRepo.findByCartId(cartId);
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    // ✅ CALCULATE: Get subtotal for cart
    public Double getCartSubtotal(Long cartId) {
        List<CartItem> cartItems = cartItemRepo.findByCartId(cartId);
        return cartItems.stream()
                .mapToDouble(item -> item.getTotalPrice().doubleValue())
                .sum();
    }

    // ✅ CONVERT: Entity to DTO (FIXED - Now handles imageUrls properly)
    public CartItemResponseDTO toDto(CartItem cartItem) {
        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setId(cartItem.getId());
        dto.setProductId(cartItem.getProduct().getId());
        dto.setProductName(cartItem.getProduct().getName());
        dto.setPricePerItem(cartItem.getPricePerItem());
        dto.setQuantity(cartItem.getQuantity());
        dto.setTotalPrice(cartItem.getTotalPrice());
        dto.setCartId(cartItem.getCart().getId());

        // ✅ FIXED: Handle product images from List<String> imageUrls
        List<String> imageUrls = cartItem.getProduct().getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            // Get the first image as the main product image
            dto.setProductImage(imageUrls.get(0));
        } else {
            // Set default image if no images available
            dto.setProductImage("/images/default-product.png");
        }

        return dto;
    }

    // ✅ CONVERT: DTO to Entity (for internal use)
    public CartItem toEntity(CartItemRequestDTO requestDTO) {
        Cart cart = cartRepo.findById(requestDTO.getCartId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        Product product = productRepo.findById(requestDTO.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(requestDTO.getQuantity());
        cartItem.setPricePerItem(product.getPrice());

        return cartItem;
    }

    // ✅ CHECK: If product exists in cart
    public boolean isProductInCart(Long cartId, Long productId) {
        return cartItemRepo.findByCartIdAndProductId(cartId, productId).isPresent();
    }

    // ✅ GET: CartItem by cart and product
    public CartItemResponseDTO getCartItemByCartAndProduct(Long cartId, Long productId) {
        CartItem cartItem = cartItemRepo.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found for cart ID: " + cartId + " and product ID: " + productId));
        return toDto(cartItem);
    }
}
