package com.My.E_CommerceApp.Controller;

import com.My.E_CommerceApp.DTO.RequestDTO.OrderItemRequestDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.OrderItemUpdateRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.OrderItemResponseDTO;
import com.My.E_CommerceApp.Service.AddressService;
import com.My.E_CommerceApp.Service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order_items")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class OderItemController {

    private final OrderItemService orderItemService;

    // ➕ Add a new item to an existing order
    @PostMapping("/{orderId}")
    public OrderItemResponseDTO addOrderItem(@PathVariable Long orderId,
                                             @RequestBody OrderItemRequestDTO dto) {
        return orderItemService.addItemToOrder(orderId, dto);
    }

    // ✏️ Update existing order item (quantity change or remove if quantity 0)
    @PutMapping("/{orderId}")
    public OrderItemResponseDTO updateOrderItem(@PathVariable Long orderId,
                                                @RequestBody OrderItemUpdateRequestDTO dto) {
        return orderItemService.updateOrderItem(orderId, dto);
    }

    // 🔍 Get all items of an order
    @GetMapping("/{orderId}")
    public List<OrderItemResponseDTO> getOrderItems(@PathVariable Long orderId) {
        return orderItemService.getOrderItemsByOrderId(orderId);
    }

    // ❌ Delete a specific order item by productId
    @DeleteMapping("/{orderId}/{productId}")
    public String deleteOrderItem(@PathVariable Long orderId,
                                  @PathVariable Long productId) {
        orderItemService.deleteOrderItem(orderId, productId);
        return "Order item deleted successfully!";
    }
}
