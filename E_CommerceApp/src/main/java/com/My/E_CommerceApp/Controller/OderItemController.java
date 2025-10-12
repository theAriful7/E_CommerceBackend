package com.My.E_CommerceApp.Controller;

import com.My.E_CommerceApp.Service.AddressService;
import com.My.E_CommerceApp.Service.OrderItemService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order_items")
public class OderItemController {

    private final OrderItemService orderItemService;

    public OderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }
}
