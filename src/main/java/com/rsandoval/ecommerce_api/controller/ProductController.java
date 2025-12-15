package com.rsandoval.ecommerce_api.controller;

import com.rsandoval.ecommerce_api.model.Product;
import com.rsandoval.ecommerce_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    // Endpoint: POST /api/products?categoryId=1
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product, @RequestParam Long categoryId) {
        Product savedProduct = productService.createProduct(product, categoryId);
        return ResponseEntity
                .created(URI.create("/api/products/"+ savedProduct.getId()))
                .body(savedProduct);
    }
}
