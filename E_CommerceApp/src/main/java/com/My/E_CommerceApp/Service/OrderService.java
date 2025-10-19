package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.OrderItemRequestDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.OrderRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.OrderItemResponseDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.OrderResponseDTO;
import com.My.E_CommerceApp.Entity.Order;
import com.My.E_CommerceApp.Entity.OrderItem;
import com.My.E_CommerceApp.Entity.Product;
import com.My.E_CommerceApp.Entity.User;
import com.My.E_CommerceApp.Enum.OrderStatus;
import com.My.E_CommerceApp.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final OrderItemRepo orderItemRepo;

    public OrderService(OrderRepo orderRepo, UserRepo userRepo,
                        ProductRepo productRepo, OrderItemRepo orderItemRepo) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.orderItemRepo = orderItemRepo;
    }

    @Transactional
    public Order createOrder(OrderRequestDTO dto) {
        // 1️⃣ Fetch user
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Create Order
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(dto.getShippingAddress());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 3️⃣ Loop through items
        for (OrderItemRequestDTO itemDTO : dto.getItems()) {
            Product product = productRepo.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(product.getPrice()); // Single price
            orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));

            totalAmount = totalAmount.add(orderItem.getTotalPrice());
            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        // 4️⃣ Save order (cascades orderItems automatically)
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
}
