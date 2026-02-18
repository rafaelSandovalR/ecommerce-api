package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.dto.order.OrderResponse;
import com.rsandoval.ecommerce_api.enums.OrderStatus;
import com.rsandoval.ecommerce_api.exception.ResourceNotFoundException;
import com.rsandoval.ecommerce_api.mapper.OrderMapper;
import com.rsandoval.ecommerce_api.model.*;
import com.rsandoval.ecommerce_api.repository.OrderRepository;
import com.rsandoval.ecommerce_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;
    private final AuthService authService;

    @Transactional
    public OrderResponse placeOrder(String shippingAddress){
        Cart cart = cartService.getCartEntity();
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot place order with empty cart");
        }
        // 1. Create the Order Shell
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PAID);
        order.setTotalPrice(cart.getTotalPrice());
        order.setShippingAddress(shippingAddress);

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
        cartService.clearCart();

        return orderMapper.toDTO(savedOrder);
    }

    public Page<OrderResponse> getUserOrders(Pageable pageable) {
        User user = authService.getCurrentUser();
        return orderRepository.findByUser(user, pageable)
                .map(orderMapper::toDTO);
    }

    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID" + orderId));

        return orderMapper.toDTO(order);
    }

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toDTO);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        // Terminal States
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("This order is already " + order.getStatus() + "and cannot be modified.");
        }
        // Trigger restock only if transitioning to CANCELLED for the first time.
        if (newStatus == OrderStatus.CANCELLED) {
            restockInventory(order.getItems());
        }

        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    private void restockInventory(List<OrderItem> items) {
        List<Product>  productsToUpdate = items.stream()
                .map(item -> {
                    Product product = item.getProduct();
                    product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                    return product;
                })
                .toList();

        productRepository.saveAll(productsToUpdate);
    }
}
