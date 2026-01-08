package com.rsandoval.ecommerce_api.dto.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String categoryName; // Flattened data, easier for frontend
}
