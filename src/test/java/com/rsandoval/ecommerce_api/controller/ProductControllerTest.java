package com.rsandoval.ecommerce_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsandoval.ecommerce_api.dto.product.ProductRequest;
import com.rsandoval.ecommerce_api.enums.Role;
import com.rsandoval.ecommerce_api.model.Category;
import com.rsandoval.ecommerce_api.model.Product;
import com.rsandoval.ecommerce_api.model.User;
import com.rsandoval.ecommerce_api.repository.CategoryRepository;
import com.rsandoval.ecommerce_api.repository.ProductRepository;
import com.rsandoval.ecommerce_api.repository.UserRepository;
import com.rsandoval.ecommerce_api.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.math.BigDecimal;

// 1. Boot up the entire Spring application
@SpringBootTest
// 2. Configure MockMvc to simulate HTTP requests without starting a real Tomcat server
@AutoConfigureMockMvc
// 3. Force Spring to use application-test.yml file
@ActiveProfiles("test")
@Transactional
public class ProductControllerTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        productRepository.deleteAll(); // Ensure a clean slate
    }

    @Test
    void testGetAllProducts_ShouldReturn200OKAndPageOfProducts() throws Exception {
        // ARRANGE: Save a real product to the H2 database
        Category category = createCategory("Electronics");

        Product existingProduct = createProduct(category, "Integration Test Laptop", "999.99", 10);

        // ACT: Use MockMvc to send a GET request to "/api/products"
        // & ASSERT: Check the HTTP status and the JSON responses
        mockMvc.perform(get("/api/products").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value(existingProduct.getName()))
                .andExpect(jsonPath("$.content[0].price").value(existingProduct.getPrice().doubleValue()))
                .andExpect(jsonPath("$.content[0].stockQuantity").value(existingProduct.getStockQuantity()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetProductById_WhenProductExists_ShouldReturn200Ok() throws Exception {
        // ARRANGE:
        // Save a Category to the database
        Category category = createCategory("Clothing");
        // Save a Product to the database
        Product existingProduct = createProduct(category, "Hoodie", "24.99", 10);

        // ACT & ASSERT
        // Perform a GET request to "/api/products/" + productId
        mockMvc.perform(get("/api/products/" + existingProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON))
        // Check HTTP status and JSON responses
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(existingProduct.getName()))
                .andExpect(jsonPath("$.price").value(existingProduct.getPrice().doubleValue()));
    }

    @Test
    void testGetProductById_WhenProductDoesNotExist_ShouldReturn404NotFound() throws Exception {
        // ARRANGE
        // Pick an ID that doesn't exist
        Long nonExistentId = 99L;
        // ACT & ASSERT
        // Perform GET request with non-existent ID
        mockMvc.perform(get("/api/products/" + nonExistentId).contentType(MediaType.APPLICATION_JSON))
        // Check HTTP status
                .andExpect(status().isNotFound());
    }

    private User createAdmin() {
        User admin = new User();
        admin.setEmail("admin@test.com");
        admin.setName("Admin User");
        admin.setPassword("encryptedMockPassword");
        admin.setRole(Role.ROLE_ADMIN);
        userRepository.save(admin);
        return admin;
    }

    private Category createCategory(String categoryName) {
        Category category = new Category();
        category.setName(categoryName);
        return categoryRepository.save(category);
    }

    private Product createProduct(Category category, String name, String price, Integer stock) {
        Product product = new Product();
        product.setCategory(category);
        product.setName(name);
        product.setPrice(new BigDecimal(price));
        product.setStockQuantity(stock);
        return productRepository.save(product);
    }

    @Test
    void testCreateProduct_AsAdmin_ShouldReturn201Created() throws Exception {
        // ARRANGE
        Category category = createCategory("Gaming");

        User admin = createAdmin();
        String token = jwtUtils.generateToken(admin.getEmail(), admin.getRole().name());

        ProductRequest request = new ProductRequest();
        request.setName("Playstation 5");
        request.setCategoryId(category.getId());
        request.setPrice(new BigDecimal("499.99"));
        request.setStockQuantity(20);

        // ACT & ASSERT
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.categoryName").value(category.getName()));
    }

    @Test
    void testUpdateProduct_AsAdmin_ShouldReturn200Ok() throws Exception {
        // ARRANGE
        Category category = createCategory("Footwear");

        User admin = createAdmin();
        String token = jwtUtils.generateToken(admin.getEmail(), admin.getRole().name());

        Product existingProduct = createProduct(category, "Nike Air Max 95", "200.00", 20);

        ProductRequest request = new ProductRequest();
        request.setName("Adidas Ultraboost");
        request.setPrice(new BigDecimal("180.00"));
        request.setStockQuantity(15);
        request.setCategoryId(category.getId());

        // ACT & ASSERT
        mockMvc.perform(put("/api/products/" + existingProduct.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.price").value(request.getPrice().doubleValue()))
                .andExpect(jsonPath("$.stockQuantity").value(request.getStockQuantity()))
                .andExpect(jsonPath("$.id").value(existingProduct.getId()));
    }
}
