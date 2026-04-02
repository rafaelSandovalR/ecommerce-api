package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.dto.order.OrderResponse;
import com.rsandoval.ecommerce_api.enums.OrderStatus;
import com.rsandoval.ecommerce_api.exception.ResourceNotFoundException;
import com.rsandoval.ecommerce_api.mapper.OrderMapper;
import com.rsandoval.ecommerce_api.model.*;
import com.rsandoval.ecommerce_api.repository.CartRepository;
import com.rsandoval.ecommerce_api.repository.OrderRepository;
import com.rsandoval.ecommerce_api.repository.ProductRepository;
import com.rsandoval.ecommerce_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;
    private final AuthService authService;

    private Order createOrderShell(User user, LocalDateTime date, OrderStatus status, BigDecimal totalPrice, String shippingAddress) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(date);
        order.setStatus(status);
        order.setTotalPrice(totalPrice);
        order.setShippingAddress(shippingAddress);
        return order;
    }

    private Order processOrder(Cart cart, Order order) {
        // Process Items (Move from Cart -> Order & Reduce Stock)
        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
        Product product = cartItem.getProduct();
        // Final Stock Check (Concurrency Safety)
        if (product.getStockQuantity() < cartItem.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }
        // Reduce Stock
        product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
        productRepository.save(product);

        // Create OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getPrice());
        return orderItem;
        }).toList();

        order.setItems(orderItems);
        return orderRepository.save(order);
    }

    // Synchronous React Checkout
    @Transactional
    public OrderResponse placeOrder(String shippingAddress){
        Cart cart = cartService.getCartEntity();
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot place order with empty cart");
        }
        // Create the Order Shell
        Order order = createOrderShell(cart.getUser(), LocalDateTime.now(), OrderStatus.PAID, cart.getTotalPrice(), shippingAddress);

        Order savedOrder = processOrder(cart, order);
        cartService.clearCart();
        return orderMapper.toDTO(savedOrder);
    }

    // Asynchronous webhook checkout
    @Transactional
    public void placeOrderFromWebhook(Long userId, String shippingAddress) {
        // Bypass the Auth context, fetch the user and cart directly
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found from webhook"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        if (cart.getItems().isEmpty()) {
            System.out.println("Webhook triggered, but cart is empty. Ignoring.");
            return;
        }

        Order order = createOrderShell(user, LocalDateTime.now(), OrderStatus.PAID, cart.getTotalPrice(), shippingAddress);
        processOrder(cart, order);

        // Clear cart manually
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
        System.out.println("Order Successfully created via Webhook for User ID: " + userId);
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
            throw new IllegalStateException("This order is already " + order.getStatus() + " and cannot be modified.");
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
