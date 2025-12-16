package com.rsandoval.ecommerce_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @Column(nullable = false)
    @NotNull(message = "Price is required") // Only checks for null
    @Positive(message = "Price must be greater than zero") // Checks logic
    private BigDecimal price;

    @Min(value = 0, message = "Stock quantity cannot be negative") // Logic Check
    private Integer stockQuantity;

    // Many Products belong to ONE Category
    @ManyToOne // TODO: Consider switching to FetchType.LAZY if Category object becomes bloated
    @JoinColumn(name = "category_id", nullable = false) // Creates FK column in the DB
    private Category category;
}
