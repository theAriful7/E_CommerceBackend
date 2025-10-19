package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.OrderItemRequestDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.OrderItemUpdateRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.OrderItemResponseDTO;
import com.My.E_CommerceApp.Entity.Order;
import com.My.E_CommerceApp.Entity.OrderItem;
import com.My.E_CommerceApp.Entity.Product;
import com.My.E_CommerceApp.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;
    private final OrderItemRepo orderItemRepo;

    // ➕ Add new item to an existing order
    @jakarta.transaction.Transactional
    public OrderItemResponseDTO saveOrderItem(Long orderId, OrderItemRequestDTO dto) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + dto.getProductId()));

        // Check if item already exists in order
        OrderItem existingItem = order.getOrderItems().stream()
                .filter(item -> item.getProduct().getId().equals(dto.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // If exists, just increase quantity
            existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());
            existingItem.setTotalPrice(existingItem.getPrice().multiply(BigDecimal.valueOf(existingItem.getQuantity())));
        } else {
            // Else, create new order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(dto.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
            order.getOrderItems().add(orderItem);
        }

        // Recalculate total amount
        BigDecimal totalAmount = order.getOrderItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        orderRepo.save(order); // Cascade saves OrderItems
        return getOrderItemDto(orderId, dto.getProductId());
    }

    // ✏️ Update existing order item quantity
    @jakarta.transaction.Transactional
    public OrderItemResponseDTO updateOrderItem(Long orderId, OrderItemUpdateRequestDTO dto) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        OrderItem orderItem = order.getOrderItems().stream()
                .filter(item -> item.getProduct().getId().equals(dto.getProductId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order item not found for productId: " + dto.getProductId()));

        if (dto.getQuantity() <= 0) {
            // Remove item if quantity 0 or less
            order.getOrderItems().remove(orderItem);
        } else {
            // Update quantity and total price
            orderItem.setQuantity(dto.getQuantity());
            orderItem.setTotalPrice(orderItem.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        }

        // Recalculate total amount
        BigDecimal totalAmount = order.getOrderItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        orderRepo.save(order);
        return getOrderItemDto(orderId, dto.getProductId());
    }

    // 🔍 Get all items of an order
    public List<OrderItemResponseDTO> getOrderItemsByOrderId(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        return order.getOrderItems().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ❌ Delete an order item
    @Transactional
    public void deleteOrderItem(Long orderId, Long productId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        OrderItem orderItem = order.getOrderItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order item not found for productId: " + productId));

        order.getOrderItems().remove(orderItem);

        // Recalculate total
        BigDecimal totalAmount = order.getOrderItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        orderRepo.save(order);
    }

    // 🔹 Helper to convert entity → DTO
    private OrderItemResponseDTO toDto(OrderItem item) {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }

    // 🔹 Helper to fetch single order item DTO by orderId + productId
    private OrderItemResponseDTO getOrderItemDto(Long orderId, Long productId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        OrderItem item = order.getOrderItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order item not found for productId: " + productId));

        return toDto(item);
    }
}
