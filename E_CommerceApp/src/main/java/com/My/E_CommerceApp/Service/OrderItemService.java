package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.OrderItemRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.OrderItemResponseDTO;
import com.My.E_CommerceApp.Entity.Order;
import com.My.E_CommerceApp.Entity.OrderItem;
import com.My.E_CommerceApp.Entity.Product;
import com.My.E_CommerceApp.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // ✅ Entity → DTO convert method
    public OrderItemResponseDTO toDto(OrderItem item) {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPricePerItem(item.getProduct().getPrice());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }

    // ✅ DTO → Entity convert method
    public OrderItem toEntity(OrderItemRequestDTO dto, Order order) {
        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found!"));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setTotalPrice(product.getPrice() * dto.getQuantity());
        return orderItem;
    }

    // ✅ Save item
    public OrderItemResponseDTO saveOrderItem(OrderItemRequestDTO dto) {

        Order order = orderRepo.findById(dto.getOrder_id())
                .orElseThrow(() -> new RuntimeException("❌ Order not found with ID: " + dto.getOrder_id()));

        OrderItem orderItem = toEntity(dto, order);
        orderItemRepo.save(orderItem);

        return toDto(orderItem);
    }

}
