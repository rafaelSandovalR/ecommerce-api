package com.rsandoval.ecommerce_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Category name is required") // Rule: String cannot be null or empty spaces
    private String name;

    // One Category has MANY products
    // TODO: (Production Readiness) Change CascadeType.ALL to CascadeType.PERSIST and MERGE for final deployment to prevent accidental deletions of associated entities.
    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;
}
