package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.enums.OrderStatus;
import com.rsandoval.ecommerce_api.mapper.OrderMapper;
import com.rsandoval.ecommerce_api.model.Order;
import com.rsandoval.ecommerce_api.model.OrderItem;
import com.rsandoval.ecommerce_api.model.Product;
import com.rsandoval.ecommerce_api.repository.OrderRepository;
import com.rsandoval.ecommerce_api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks // Create a real OrderService but inject the "fake" repositories above
    private OrderService orderService;

    @Test
    void testUpdateOrderStatus_WhenDelivered_ShouldThrowException() {
        // ARRANGE
        Order deliveredOrder = new Order();
        deliveredOrder.setId(1L);
        deliveredOrder.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(deliveredOrder));

        // ACT & ASSERT
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderService.updateOrderStatus(1L, "PAID");
        });

        assertTrue(exception.getMessage().contains("cannot be modified"));

        // Ensure the database save was NEVER called
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testUpdateOrderStatus_WhenCancelled_ShouldRestockInventory() {
        // ARRANGE
        Product product = new Product();
        product.setId(10L);
        product.setStockQuantity(5);

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(3);

        Order activeOrder = new Order();
        activeOrder.setId(2L);
        activeOrder.setStatus(OrderStatus.PAID);
        activeOrder.setItems(List.of(item));

        when(orderRepository.findById(2L)).thenReturn(Optional.of(activeOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(activeOrder);
        when(orderMapper.toDTO(any(Order.class))).thenReturn(null);

        // ACT
        orderService.updateOrderStatus(2L, "CANCELLED");

        // ASSERT
        assertEquals(OrderStatus.CANCELLED, activeOrder.getStatus());
        assertEquals(8, product.getStockQuantity()); // 5 original + 3 restocked

        verify(productRepository, times(1)).saveAll(anyList());
    }
}
