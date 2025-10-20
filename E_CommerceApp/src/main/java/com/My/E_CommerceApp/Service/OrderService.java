package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.OrderItemRequestDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.OrderRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.OrderItemResponseDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.OrderResponseDTO;
import com.My.E_CommerceApp.Entity.*;
import com.My.E_CommerceApp.Enum.OrderStatus;
import com.My.E_CommerceApp.Exception.CustomException.ResourceNotFoundException;
import com.My.E_CommerceApp.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final OrderItemRepo orderItemRepo;
    private final AddressRepo addressRepo;
    private final CartItemRepo cartItemRepo;

    @Transactional
    public Order createOrder(OrderRequestDTO dto) {
        // 1️⃣ Fetch user
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

        // 2️⃣ Fetch shipping address
        Address address = addressRepo.findById(dto.getShippingAddressId())
                .orElseThrow(() -> new RuntimeException("Shipping address not found with id: " + dto.getShippingAddressId()));

        // 3️⃣ Create new Order
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(address);
        order.setCreatedAt(dto.getCreatedAt());
        order.setUpdatedAt(dto.getUpdatedAt());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 4️⃣ Add Order Items
        for (OrderItemRequestDTO itemDTO : dto.getItems()) {
            Product product = productRepo.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemDTO.getProductId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));

            totalAmount = totalAmount.add(orderItem.getTotalPrice());
            orderItems.add(orderItem);
        }

        // 5️⃣ Set totals and save order
        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        return orderRepo.save(order);
    }

    // ✅ Convert Entity → DTO
    public OrderResponseDTO toDto(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserId(order.getUser().getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setOrderDate(order.getCreatedAt()); // Use Base.createdAt

        List<OrderItemResponseDTO> itemDTOs = order.getOrderItems().stream().map(item -> {
            OrderItemResponseDTO i = new OrderItemResponseDTO();
            i.setProductId(item.getProduct().getId());
            i.setProductName(item.getProduct().getName());
            i.setQuantity(item.getQuantity());
            i.setPrice(item.getPrice());
            i.setTotalPrice(item.getTotalPrice());
            return i;
        }).collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }

    // ✅ Save Order + Return DTO
    @Transactional
    public OrderResponseDTO save(OrderRequestDTO dto) {
        Order order = createOrder(dto);
        return toDto(order);
    }

    // ✅ Find by ID
    public OrderResponseDTO findById(Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return toDto(order);
    }

    // ✅ Find all Orders
    public List<OrderResponseDTO> findAll() {
        return orderRepo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Update Status
    @Transactional
    public OrderResponseDTO updateStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(newStatus);
        return toDto(orderRepo.save(order));
    }

    // ✅ Delete
    @Transactional
    public void delete(Long id) {
        orderRepo.deleteById(id);
    }

    @Transactional
    public OrderResponseDTO checkout(Long userId, Long addressId) {
        // 1️⃣ Fetch User
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // 2️⃣ Fetch Shipping Address
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Shipping address not found with id: " + addressId));

        // 3️⃣ Fetch Cart Items
        List<CartItem> cartItems = cartItemRepo.findByUser_Id(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty. Add products before checkout.");
        }

        // 4️⃣ Create new Order
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(address);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 5️⃣ Convert CartItems → OrderItems
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());

            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItem.setTotalPrice(cartItem.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            totalAmount = totalAmount.add(orderItem.getTotalPrice());
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        // 6️⃣ Save Order (cascades OrderItems)
        Order savedOrder = orderRepo.save(order);

        // 7️⃣ Clear User's Cart
        cartItemRepo.deleteAll(cartItems);

        // 8️⃣ Return OrderResponseDTO
        return this.toDto(savedOrder);
    }


}
