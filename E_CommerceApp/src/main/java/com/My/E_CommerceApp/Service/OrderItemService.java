package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.OrderItemRequestDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.OrderItemUpdateRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.OrderItemResponseDTO;
import com.My.E_CommerceApp.Entity.Order;
import com.My.E_CommerceApp.Entity.OrderItem;
import com.My.E_CommerceApp.Entity.Product;
import com.My.E_CommerceApp.Enum.OrderStatus;
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

    // ✅ ADD ITEM TO EXISTING ORDER
    @Transactional
    public OrderItemResponseDTO addItemToOrder(Long orderId, OrderItemRequestDTO dto) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Check if order can be modified
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Cannot modify order with status: " + order.getStatus());
        }

        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check stock
        if (product.getStock() < dto.getQuantity()) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        // Check if item already exists
        OrderItem existingItem = orderItemRepo.findByOrderAndProduct(order, product)
                .orElse(null);

        if (existingItem != null) {
            // Update existing item
            existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());
            existingItem.setTotalPrice(existingItem.getPrice().multiply(BigDecimal.valueOf(existingItem.getQuantity())));
        } else {
            // Create new order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(dto.getQuantity());
            orderItem.setPrice(product.getPrice()); // Use current price for new additions
            orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));

            order.getOrderItems().add(orderItem);
        }

        // Update order total and product stock
        updateOrderTotal(order);
        updateProductStock(product, -dto.getQuantity()); // Reduce stock

        orderRepo.save(order);
        return getOrderItemResponse(orderId, dto.getProductId());
    }

    // ✅ UPDATE ORDER ITEM QUANTITY
    @Transactional
    public OrderItemResponseDTO updateOrderItem(Long orderId, OrderItemUpdateRequestDTO dto) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Cannot modify order with status: " + order.getStatus());
        }

        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        OrderItem orderItem = orderItemRepo.findByOrderAndProduct(order, product)
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        int quantityDifference = dto.getQuantity() - orderItem.getQuantity();

        // Check stock for increase
        if (quantityDifference > 0 && product.getStock() < quantityDifference) {
            throw new RuntimeException("Insufficient stock for quantity increase");
        }

        if (dto.getQuantity() <= 0) {
            // Remove item if quantity is 0 or less
            return removeItemFromOrder(orderId, dto.getProductId());
        }

        // Update quantity and total
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setTotalPrice(orderItem.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));

        // Update stock
        updateProductStock(product, -quantityDifference);

        updateOrderTotal(order);
        orderRepo.save(order);

        return toDto(orderItem);
    }

    // ✅ REMOVE ITEM FROM ORDER
    @Transactional
    public OrderItemResponseDTO removeItemFromOrder(Long orderId, Long productId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Cannot modify order with status: " + order.getStatus());
        }

        OrderItem orderItem = orderItemRepo.findByOrderAndProduct(order, productRepo.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found")))
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        // Restore stock
        updateProductStock(orderItem.getProduct(), orderItem.getQuantity());

        // Remove item
        order.getOrderItems().remove(orderItem);
        orderItemRepo.delete(orderItem);

        updateOrderTotal(order);
        orderRepo.save(order);

        return toDto(orderItem); // Return the removed item details
    }

    // ✅ GET ORDER ITEMS
    public List<OrderItemResponseDTO> getOrderItemsByOrderId(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return order.getOrderItems().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ UPDATE ORDER TOTAL
    private void updateOrderTotal(Order order) {
        BigDecimal total = order.getOrderItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
    }

    // ✅ UPDATE PRODUCT STOCK
    private void updateProductStock(Product product, int quantityChange) {
        product.setStock(product.getStock() + quantityChange);
        productRepo.save(product);
    }

    // ✅ CONVERT TO DTO
    private OrderItemResponseDTO toDto(OrderItem item) {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setTotalPrice(item.getTotalPrice());

        if (!item.getProduct().getImageUrls().isEmpty()) {
            dto.setProductImage(item.getProduct().getImageUrls().get(0));
        }

        return dto;
    }

    // ✅ GET ORDER ITEM RESPONSE
    private OrderItemResponseDTO getOrderItemResponse(Long orderId, Long productId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderItem item = orderItemRepo.findByOrderAndProduct(order, productRepo.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found")))
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        return toDto(item);
    }

    @Transactional
    public void deleteOrderItem(Long orderId, Long productId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Cannot modify order with status: " + order.getStatus());
        }

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        OrderItem orderItem = orderItemRepo.findByOrderAndProduct(order, product)
                .orElseThrow(() -> new RuntimeException("Order item not found for product ID: " + productId));

        // Restore product stock
        updateProductStock(product, orderItem.getQuantity());

        // Remove item from order
        order.getOrderItems().remove(orderItem);
        orderItemRepo.delete(orderItem);

        // Update order total
        updateOrderTotal(order);
        orderRepo.save(order);
    }
}
