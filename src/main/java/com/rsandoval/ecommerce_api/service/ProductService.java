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

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }

    public Product createProduct(Product product, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));

        // Set the relationship
        product.setCategory(category);

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product productToUpdate = getProductById(id);

        productToUpdate.setName(productDetails.getName());
        productToUpdate.setDescription(productDetails.getDescription());
        productToUpdate.setPrice(productDetails.getPrice());
        productToUpdate.setStockQuantity(productDetails.getStockQuantity());

        // SAFE CATEGORY UPDATE LOGIC:
        // TODO: Might want to restrict this ability in production
        if (productDetails.getCategory() != null && productDetails.getCategory().getId() != null) {
            Long newCategoryId = productDetails.getCategory().getId();

            Category newCategory = categoryRepository.findById(newCategoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + newCategoryId));

            productToUpdate.setCategory(newCategory);
        }

        return productRepository.save(productToUpdate);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with ID: " + id);
        }

        productRepository.deleteById(id);
    }
}
