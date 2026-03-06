package com.rsandoval.ecommerce_api.controller;

import com.rsandoval.ecommerce_api.model.Category;
import com.rsandoval.ecommerce_api.model.Product;
import com.rsandoval.ecommerce_api.repository.CategoryRepository;
import com.rsandoval.ecommerce_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll(); // Ensure a clean slate
    }

    @Test
    void testGetAllProducts_ShouldReturn200OKAndPageOfProducts() throws Exception {
        // ARRANGE: Save a real product to the H2 database
        String productName = "Integration Test Laptop";
        BigDecimal price = new BigDecimal("999.99");
        Integer stockQty = 10;

        Category category = new Category();
        category.setName("Electronics");
        categoryRepository.save(category);

        Product product = new Product();
        product.setName(productName);
        product.setPrice(price  );
        product.setStockQuantity(stockQty);
        product.setCategory(category);
        productRepository.save(product);

        // ACT: Use MockMvc to send a GET request to "/api/products"
        // & ASSERT: Check the HTTP status and the JSON responses
        mockMvc.perform(get("/api/products").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value(productName))
                .andExpect(jsonPath("$.content[0].price").value(price))
                .andExpect(jsonPath("$.content[0].stockQuantity").value(stockQty))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
