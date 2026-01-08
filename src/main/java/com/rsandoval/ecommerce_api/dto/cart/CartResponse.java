package com.rsandoval.ecommerce_api.dto.cart;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponse {
    private Long id;
    private BigDecimal totalPrice;
    private List<CartItemDto> items;
}
