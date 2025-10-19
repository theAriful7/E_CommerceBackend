package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.OrderItemRequestDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.OrderItemUpdateRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.OrderItemResponseDTO;
import com.My.E_CommerceApp.Entity.Order;
import com.My.E_CommerceApp.Entity.OrderItem;
import com.My.E_CommerceApp.Entity.Product;
import com.My.E_CommerceApp.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemService {
    private final OrderItemRepo orderItemRepo;
    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;

    public OrderItemService(OrderItemRepo orderItemRepo, ProductRepo productRepo, OrderRepo orderRepo) {
        this.orderItemRepo = orderItemRepo;
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
    }

    // ✅ Entity → DTO convert
    public OrderItemResponseDTO toDto(OrderItem item) {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }

    // ✅ DTO → Entity convert for add
    public OrderItem toEntity(OrderItemRequestDTO dto, Order order) {
        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found!"));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setPrice(product.getPrice());
        orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        return orderItem;
    }

    // ✅ Save new order item
    @Transactional
    public OrderItemResponseDTO saveOrderItem(Long orderId, OrderItemRequestDTO dto) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        OrderItem orderItem = toEntity(dto, order);
        orderItemRepo.save(orderItem);

        return toDto(orderItem);
    }

    // ✅ Update existing order item
    @Transactional
    public OrderItemResponseDTO updateOrderItem(Long orderId, OrderItemUpdateRequestDTO dto) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        OrderItem item = orderItemRepo.findByOrderAndProduct(order,
                        productRepo.findById(dto.getProductId())
                                .orElseThrow(() -> new RuntimeException("Product not found!")))
                .orElseThrow(() -> new RuntimeException("OrderItem not found for this order and product"));

        item.setQuantity(dto.getQuantity());
        item.setTotalPrice(item.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));

        orderItemRepo.save(item);
        return toDto(item);
    }

    // ✅ Get all items of an order
    public List<OrderItemResponseDTO> getOrderItemsByOrderId(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        return orderItemRepo.findByOrder(order)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Delete order item by orderId + productId
    @Transactional
    public void deleteOrderItem(Long orderId, Long productId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        OrderItem item = orderItemRepo.findByOrderAndProduct(order,
                        productRepo.findById(productId)
                                .orElseThrow(() -> new RuntimeException("Product not found!")))
                .orElseThrow(() -> new RuntimeException("OrderItem not found for this order and product"));

        orderItemRepo.delete(item);
    }

}
