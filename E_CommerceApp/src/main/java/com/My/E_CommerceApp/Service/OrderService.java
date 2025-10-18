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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepo orderRepo;

    public OrderService(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private OrderItemRepo orderItemRepo;


    public Order createOrder(OrderRequestDTO dto) {
        User user = userRepo.findById(dto.getUserId()).orElseThrow();
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(dto.getShippingAddress());
        order.setStatus(OrderStatus.PENDING);

        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequestDTO itemDTO : dto.getItems()) {
            Product product = productRepo.findById(itemDTO.getProductId()).orElseThrow();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setTotalPrice(product.getPrice() * itemDTO.getQuantity());

            totalAmount += orderItem.getTotalPrice();
            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepo.save(order);
        orderItemRepo.saveAll(orderItems);

        return savedOrder;
    }


    // ✅ Convert Entity → DTO
    public OrderResponseDTO toDto(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserId(order.getUser().getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setOrderDate(order.getOrderDate());
        dto.setShippingAddress(order.getShippingAddress());

        List<OrderItemResponseDTO> itemDTOs = new ArrayList<>();
        for (OrderItem item : order.getOrderItems()) {
            OrderItemResponseDTO i = new OrderItemResponseDTO();
            i.setProductId(item.getProduct().getId());
            i.setProductName(item.getProduct().getName());
            i.setQuantity(item.getQuantity());
            i.setPricePerItem(item.getProduct().getPrice());
            i.setTotalPrice(item.getTotalPrice());
            itemDTOs.add(i);
        }

        dto.setItems(itemDTOs);
        return dto;
    }

    // ✅ Save Order + Return DTO
    public OrderResponseDTO save(OrderRequestDTO dto) {
        return toDto(createOrder(dto));
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

    // ✅ Update Status (optional)
    public OrderResponseDTO updateStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(newStatus);
        return toDto(orderRepo.save(order));
    }

    // ✅ Delete
    public void delete(Long id) {
        orderRepo.deleteById(id);
    }
}
