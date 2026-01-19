package com.rsandoval.ecommerce_api.mapper;

import com.rsandoval.ecommerce_api.dto.order.OrderItemDto;
import com.rsandoval.ecommerce_api.dto.order.OrderResponse;
import com.rsandoval.ecommerce_api.model.Order;
import com.rsandoval.ecommerce_api.model.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toDTO(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getStatus().name());
        dto.setShippingAddress(order.getShippingAddress());

        List<OrderItemDto> itemDtos = order.getItems()
                .stream()
                .map(this::toItemDTO)
                .toList();

        dto.setItems(itemDtos);
        return dto;
    }

    public OrderItemDto toItemDTO(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPricePerUnit(item.getPrice());
        dto.setTotalLinePrice(
                item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
        );
        return dto;
    }
}
