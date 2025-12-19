package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.dto.CartItemDto;
import com.rsandoval.ecommerce_api.dto.CartResponse;
import com.rsandoval.ecommerce_api.model.Cart;
import com.rsandoval.ecommerce_api.model.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartMapper {

    public CartResponse toDTO(Cart cart) {
        CartResponse dto = new CartResponse();
        dto.setId(cart.getId());
        dto.setTotalPrice(cart.getTotalPrice());

        List<CartItemDto> itemDtos = cart.getItems()
                .stream()
                .map(this::toCartItemDTO)
                .toList();

        dto.setItems(itemDtos);

        return dto;
    }

    public CartItemDto toCartItemDTO(CartItem item) {
        CartItemDto dto = new CartItemDto();
        dto.setId(item.getId());
        dto.setProductId(item.getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());

        // Calculate subtotal for display
        BigDecimal subTotal = item.getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return dto;
    }
}
