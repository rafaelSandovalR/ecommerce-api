package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.model.Category;
import com.rsandoval.ecommerce_api.model.Product;
import com.rsandoval.ecommerce_api.repository.CategoryRepository;
import com.rsandoval.ecommerce_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public Product createProduct(Product product, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        // Set the relationship
        product.setCategory(category);

        return productRepository.save(product);
    }
}
