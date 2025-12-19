package com.rsandoval.ecommerce_api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDto {
    private Long id; // CartItem ID (useful for removal)
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price; // Price per unit
    private BigDecimal subtotal; // Calculated
}
