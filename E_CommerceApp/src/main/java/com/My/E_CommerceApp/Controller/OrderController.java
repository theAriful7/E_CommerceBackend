package com.My.E_CommerceApp.Controller;

import com.My.E_CommerceApp.DTO.RequestDTO.OrderRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.OrderResponseDTO;
import com.My.E_CommerceApp.Enum.OrderStatus;
import com.My.E_CommerceApp.Service.AddressService;
import com.My.E_CommerceApp.Service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ✅ Create new order
    @PostMapping
    public OrderResponseDTO createOrder(@RequestBody OrderRequestDTO dto) {
        return orderService.save(dto);
    }

    // ✅ Get all orders
    @GetMapping
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.findAll();
    }

    // ✅ Get order by id
    @GetMapping("/{id}")
    public OrderResponseDTO getOrderById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    // ✅ Update order status
    @PutMapping("/{id}/status")
    public OrderResponseDTO updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return orderService.updateStatus(id, status);
    }

    // ✅ Delete order
    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
        return "Order deleted successfully!";
    }
}
