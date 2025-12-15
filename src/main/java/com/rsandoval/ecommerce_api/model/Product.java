package com.rsandoval.ecommerce_api.model;

import jakarta.persistence.*;
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
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private Integer stockQuantity;

    // Many Products belong to ONE Category
    @ManyToOne // TODO: Consider switching to FetchType.LAZY if Category object becomes bloated
    @JoinColumn(name = "category_id", nullable = false) // Creates FK column in the DB
    private Category category;
}
