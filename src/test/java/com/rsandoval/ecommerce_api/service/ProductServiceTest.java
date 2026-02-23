package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.dto.product.ProductResponse;
import com.rsandoval.ecommerce_api.mapper.ProductMapper;
import com.rsandoval.ecommerce_api.model.Product;
import com.rsandoval.ecommerce_api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void testGetAllProducts_WithValidCriteria_ShouldReturnPagedProducts() {
        // ARRANGE
        String keyword = null;
        Long categoryId = 1L;
        BigDecimal minPrice = new BigDecimal("10.00");
        BigDecimal maxPrice = new BigDecimal("50.00");
        Pageable pageable = PageRequest.of(0,10);

        Product mockProduct = new Product();
        mockProduct.setId(10L);
        mockProduct.setName("Smartphone");

        Page<Product> mockProductPage = new PageImpl<>(List.of(mockProduct));

        ProductResponse mockResponse = new ProductResponse();
        mockResponse.setId(10L);
        mockResponse.setName("Smartphone");

        when(productRepository.searchProducts(null, categoryId, minPrice, maxPrice, pageable))
                .thenReturn(mockProductPage);
        when(productMapper.toDTO(any(Product.class))).thenReturn(mockResponse);

        // ACT
        Page<ProductResponse> result = productService.getAllProducts(
                keyword, categoryId, minPrice, maxPrice, pageable
        );

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Smartphone", result.getContent().get(0).getName());
        verify(productRepository, times(1))
                .searchProducts(null, categoryId, minPrice, maxPrice, pageable);
    }

    @Test
    void testUpdateProduct_WhenProductAndCategoryExist_ShouldUpdateFieldsAndSave() {
        // ARRANGE
        // ACT
        // ASSERT
    }
}
