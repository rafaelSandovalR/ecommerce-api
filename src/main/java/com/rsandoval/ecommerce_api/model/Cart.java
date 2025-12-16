package com.rsandoval.ecommerce_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One User has EXACTLY One Cart
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // A Cart has many Items
    // orphanRemoval: If I remove an item from this list, delete it from the DB
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    // Store total price to avoid recalculation
    @PositiveOrZero // Allows 0 (empty cart) by blocks negative numbers
    private BigDecimal totalPrice = BigDecimal.ZERO;
}
