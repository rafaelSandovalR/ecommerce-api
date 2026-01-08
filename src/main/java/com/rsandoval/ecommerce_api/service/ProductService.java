package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.dto.product.ProductRequest;
import com.rsandoval.ecommerce_api.dto.product.ProductResponse;
import com.rsandoval.ecommerce_api.exception.ResourceNotFoundException;
import com.rsandoval.ecommerce_api.mapper.ProductMapper;
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
    private final ProductMapper productMapper;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDTO)
                .toList();
    }

    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(productMapper::toDTO)
                .toList();
    }

    public ProductResponse getProductById(Long productId) {
        Product product = getProductEntity(productId);
        return productMapper.toDTO(product);
    }

    public ProductResponse createProduct(ProductRequest request) {
        Category category = getCategoryEntity(request.getCategoryId());

        Product product = productMapper.toEntity(request, category);
        Product savedProduct = productRepository.save(product);

        return productMapper.toDTO(savedProduct);
    }

    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product productToUpdate = getProductEntity(productId);

        productToUpdate.setName(request.getName());
        productToUpdate.setDescription(request.getDescription());
        productToUpdate.setPrice(request.getPrice());
        productToUpdate.setStockQuantity(request.getStockQuantity());

        // TODO: Might want to restrict this ability in production
        Category newCategory = getCategoryEntity(request.getCategoryId());
        productToUpdate.setCategory(newCategory);

        Product updatedProduct = productRepository.save(productToUpdate);
        return productMapper.toDTO(updatedProduct);
    }

    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }

        productRepository.deleteById(productId);
    }

    private Category getCategoryEntity(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
    }

    private Product getProductEntity(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }

}
