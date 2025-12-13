package com.rsandoval.ecommerce_api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    // One Category has MANY products
    // TODO: (Production Readiness) Change CascadeType.ALL to CascadeType.PERSIST and MERGE for final deployment to prevent accidental deletions of associated entities.
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;
}
