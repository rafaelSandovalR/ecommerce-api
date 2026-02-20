package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.dto.cart.CartRequest;
import com.rsandoval.ecommerce_api.mapper.CartMapper;
import com.rsandoval.ecommerce_api.model.Cart;
import com.rsandoval.ecommerce_api.model.CartItem;
import com.rsandoval.ecommerce_api.model.Product;
import com.rsandoval.ecommerce_api.model.User;
import com.rsandoval.ecommerce_api.repository.CartRepository;
import com.rsandoval.ecommerce_api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private CartService cartService;

    @Test
    void testAddItemToCart_WhenInsufficientStock_ShouldThrowException() {
        // ARRANGE
        User mockUser = new User();
        Cart mockCart = new Cart();
        mockCart.setUser(mockUser);
        mockCart.setItems(new ArrayList<>());

        when(authService.getCurrentUser()).thenReturn(mockUser);
        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.of(mockCart));

        Product mockProduct = new Product();
        mockProduct.setId(5L);
        mockProduct.setStockQuantity(2);

        when(productRepository.findById(5L)).thenReturn(Optional.of(mockProduct));

        CartRequest request = new CartRequest();
        request.setProductId(5L);
        request.setQuantity(10);

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.addItemToCart(request);
        });
        assertTrue(exception.getMessage().contains("Insufficient stock"));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void testUpdateItemQuantity_ShouldUpdatePriceAndRecalculateTotal() {
        // ARRANGE
        User mockUser = new User();
        // Product setup
        Product mockProduct = new Product();
        mockProduct.setId(10L);
        mockProduct.setPrice(new BigDecimal("25.00"));
        mockProduct.setStockQuantity(50);
        // Put an item in the cart
        CartItem existingItem = new CartItem();
        existingItem.setProduct(mockProduct);
        existingItem.setQuantity(1);
        existingItem.setPrice(new BigDecimal("25.00"));

        Cart mockCart = new Cart();
        mockCart.setUser(mockUser);
        mockCart.setItems(new ArrayList<>(List.of(existingItem))); // Needs to be a mutable list

        when(authService.getCurrentUser()).thenReturn(mockUser);
        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.of(mockCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);
        when(cartMapper.toDTO(any(Cart.class))).thenReturn(null);

        // Qty Update Request
        CartRequest request = new CartRequest();
        request.setProductId(10L);
        request.setQuantity(3);

        // ACT
        cartService.updateItemQuantity(request);

        // ASSERT
        assertEquals(3, existingItem.getQuantity());
        assertEquals(new BigDecimal("75.00"), mockCart.getTotalPrice());
        verify(cartRepository, times(1)).save(mockCart);
    }

}
