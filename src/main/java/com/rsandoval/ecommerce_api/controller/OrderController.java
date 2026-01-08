package com.rsandoval.ecommerce_api.controller;

import com.rsandoval.ecommerce_api.dto.order.OrderResponse;
import com.rsandoval.ecommerce_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}/place")
    public ResponseEntity<OrderResponse> placeOrder(@PathVariable Long userId) {
        OrderResponse placedOrder = orderService.placeOrder(userId);
        return ResponseEntity
                .created(URI.create("/api/orders/" + placedOrder.getId()))
                .body(placedOrder);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }
}
