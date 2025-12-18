package com.rsandoval.ecommerce_api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal pricePerUnit;    // Snapshot price
    private BigDecimal totalLinePrice;  // Calculated field
}
