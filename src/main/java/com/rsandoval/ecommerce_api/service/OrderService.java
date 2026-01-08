package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.dto.order.OrderResponse;
import com.rsandoval.ecommerce_api.enums.OrderStatus;
import com.rsandoval.ecommerce_api.exception.ResourceNotFoundException;
import com.rsandoval.ecommerce_api.mapper.OrderMapper;
import com.rsandoval.ecommerce_api.model.*;
import com.rsandoval.ecommerce_api.repository.OrderRepository;
import com.rsandoval.ecommerce_api.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse placeOrder(Long userId){
        Cart cart = cartService.getCartEntity(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot place order with empty cart");
        }
        // 1. Create the Order Shell
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(cart.getTotalPrice());

        // 2. Process Items (Move from Cart -> Order & Reduce Stock) (Use stream)
        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            // 2a. Final Stock Check (Concurrency Safety)
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }
            // 2b. Reduce Stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            // 2c. Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            return orderItem;
        }).toList();

        order.setItems(orderItems);

        // 3. Save Order
        Order savedOrder = orderRepository.save(order);

        // 4. Clear Cart
        cartService.clearCart(userId);

        return orderMapper.toDTO(savedOrder);
    }

    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID" + orderId));

        return orderMapper.toDTO(order);
    }
}
