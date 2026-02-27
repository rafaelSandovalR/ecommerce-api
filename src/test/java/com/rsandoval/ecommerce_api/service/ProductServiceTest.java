package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.dto.product.ProductRequest;
import com.rsandoval.ecommerce_api.dto.product.ProductResponse;
import com.rsandoval.ecommerce_api.exception.ResourceNotFoundException;
import com.rsandoval.ecommerce_api.mapper.ProductMapper;
import com.rsandoval.ecommerce_api.model.Category;
import com.rsandoval.ecommerce_api.model.Product;
import com.rsandoval.ecommerce_api.repository.CategoryRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

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
        String productName = "Smartphone";
        Long productId = 10L;
        mockProduct.setId(productId);
        mockProduct.setName(productName);

        Page<Product> mockProductPage = new PageImpl<>(List.of(mockProduct));

        ProductResponse mockResponse = new ProductResponse();
        mockResponse.setId(productId);
        mockResponse.setName(productName);

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
        assertEquals(productName, result.getContent().get(0).getName());
        verify(productRepository, times(1))
                .searchProducts(null, categoryId, minPrice, maxPrice, pageable);
    }

    @Test
    void testGetAllProducts_WithKeyword_ShouldFormatSearchPatternCorrectly() {
        /*
            Why: We have specific logic ("%" + keyword.toLowerCase() + "%") to format the search.
            This test proves that if a user types "  ApPle ", it correctly queries for %apple%.
        */
        // ARRANGE
        String keyword = "ApPle";
        String expectedSearchPattern = "%apple%";
        String productName = "Apple";
        Long categoryId = 1L;
        Long productId = 5L;
        BigDecimal minPrice = new BigDecimal("2.00");
        BigDecimal maxPrice = new BigDecimal("10.00");
        Pageable pageable = PageRequest.of(0, 10);

        Product mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setName(productName);

        Page<Product> mockProductPage = new PageImpl<>(List.of(mockProduct));

        ProductResponse mockResponse = new ProductResponse();
        mockResponse.setId(productId);
        mockResponse.setName(productName);

        when(productRepository.searchProducts(expectedSearchPattern, categoryId, minPrice, maxPrice, pageable))
                .thenReturn(mockProductPage);
        when(productMapper.toDTO(any(Product.class))).thenReturn(mockResponse);

        // ACT
        Page<ProductResponse> result = productService.getAllProducts(
                keyword, categoryId, minPrice, maxPrice, pageable
        );
        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(productName, result.getContent().get(0).getName());
        verify(productRepository, times(1))
                .searchProducts(expectedSearchPattern, categoryId, minPrice, maxPrice, pageable);
    }

    @Test
    void testGetProductById_WhenProductExists_ShouldReturnProductResponse() {
        // ARRANGE
        Product mockProduct = new Product();
        Long productId = 1L;
        String productName = "T-Shirt";
        mockProduct.setId(productId);
        mockProduct.setName(productName);

        ProductResponse mockResponse = new ProductResponse();
        mockResponse.setId(productId);
        mockResponse.setName(productName);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(productMapper.toDTO(any(Product.class))).thenReturn(mockResponse);
        // ACT
        ProductResponse result = productService.getProductById(productId);

        // ASSERT
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals(productName, result.getName());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testGetProductById_WhenProductDoesNotExist_ShouldThrowResourceNotFoundException() {
        // ARRANGE
        Long nonExistentProductId = 99L;

        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());
        // ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(nonExistentProductId);
        });

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(productRepository, times(1)).findById(nonExistentProductId);
        verify(productMapper, never()).toDTO(any());
    }

    @Test
    void testCreateProduct_WithValidCategory_ShouldSaveAndReturnProduct() {
        // ARRANGE
        Category mockCategory = new Category();
        Long categoryId = 1L;
        String categoryName = "Book";
        mockCategory.setId(categoryId);
        mockCategory.setName(categoryName);

        Product mockProduct = new Product();
        Long productId = 5L;
        String productName = "A Knight of the Seven Kingdoms";
        mockProduct.setId(productId);
        mockProduct.setName(productName);

        ProductRequest request = new ProductRequest();
        request.setCategoryId(categoryId);
        request.setName(productName);

        ProductResponse mockResponse = new ProductResponse();
        mockResponse.setName(productName);
        mockResponse.setId(productId);
        mockResponse.setCategoryName(categoryName);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(productMapper.toEntity(request, mockCategory)).thenReturn(mockProduct);
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);
        when(productMapper.toDTO(any(Product.class))).thenReturn(mockResponse);
        // ACT
        ProductResponse result = productService.createProduct(request);
        // ASSERT
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals(productName, result.getName());
        assertEquals(categoryName, result.getCategoryName());
        verify(productRepository, times(1)).save(mockProduct);
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testCreateProduct_WhenCategoryDoesNotExist_ShouldThrowResourceNotFoundException() {
        /*
            Why: Because getCategoryEntity helper throws an exception if the DB comes back empty,
            we need a test to prove the creation stops immediately and doesn't try to save a product with a null category.
        */
        // ARRANGE
        Long nonExistentId = 99L;
        ProductRequest request = new ProductRequest();
        request.setName("Plutonium");
        request.setCategoryId(nonExistentId);

        when(categoryRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.createProduct(request);
        });

        assertTrue(exception.getMessage().contains("Category not found"));
        verify(categoryRepository, times(1)).findById(nonExistentId);
        verify(productMapper, never()).toEntity(any(), any());
        verify(productRepository, never()).save(any());
    }


    @Test
    void testUpdateProduct_WhenProductAndCategoryExist_ShouldUpdateFieldsAndSave() {
        // ARRANGE
        Long categoryId = 1L;
        String categoryName = "Electronics";
        Long productId = 5L;
        String updatedName = "iPhone 17";
        BigDecimal updatedPrice = new BigDecimal("999.99");
        Integer updatedQty = 25;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Smartphone");
        existingProduct.setPrice(new BigDecimal("499.99"));
        existingProduct.setStockQuantity(15);

        Category newCategory = new Category();
        newCategory.setId(categoryId);
        newCategory.setName(categoryName);

        ProductRequest request = new ProductRequest();
        request.setName(updatedName);
        request.setCategoryId(categoryId);
        request.setPrice(updatedPrice);
        request.setStockQuantity(updatedQty);

        ProductResponse mockResponse = new ProductResponse();
        mockResponse.setId(productId);
        mockResponse.setName(updatedName);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(newCategory));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);
        when(productMapper.toDTO(existingProduct)).thenReturn(mockResponse);

        // ACT
        ProductResponse result = productService.updateProduct(productId, request);

        // ASSERT
        assertNotNull(result);
        assertEquals(updatedName, existingProduct.getName());
        assertEquals(updatedPrice, existingProduct.getPrice());
        assertEquals(updatedQty, existingProduct.getStockQuantity());
        assertEquals(newCategory, existingProduct.getCategory());

        verify(productRepository, times(1)).findById(productId);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    void testUpdateProduct_WhenProductDoesNotExist_ShouldThrowResourceNotFoundException() {
        // ARRANGE
        Long nonExistentId = 999L;
        ProductRequest request = new ProductRequest();
        request.setName("Polonium");

        when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        // ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProduct(nonExistentId, request);
        });

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(productRepository, times(1)).findById(nonExistentId);
        verify(categoryRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void testDeleteProduct_WhenProductExists_ShouldSetDeletedFlagToTrue() {
    /*
        Why: We are doing a Soft Delete (product.setDeleted(true)), not a hard delete from the database.
        This test will use Mockito's verify to ensure productRepository.delete() is never called,
        but productRepository.save() is called with a product that has isDeleted() == true.
     */
        // ARRANGE
        // ACT
        // ASSERT
    }

    @Test
    void testDeleteProduct_WhenProductDoesNotExist_ShouldThrowResourceNotFoundException() {
        // ARRANGE
        // ACT
        // ASSERT
    }
}
