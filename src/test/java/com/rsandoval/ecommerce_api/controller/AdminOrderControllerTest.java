package com.rsandoval.ecommerce_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.rsandoval.ecommerce_api.dto.cart.CartRequest;
import com.rsandoval.ecommerce_api.dto.order.PlaceOrderRequest;
import com.rsandoval.ecommerce_api.enums.Role;
import com.rsandoval.ecommerce_api.model.Category;
import com.rsandoval.ecommerce_api.model.Product;
import com.rsandoval.ecommerce_api.model.User;
import com.rsandoval.ecommerce_api.repository.CategoryRepository;
import com.rsandoval.ecommerce_api.repository.ProductRepository;
import com.rsandoval.ecommerce_api.repository.UserRepository;
import com.rsandoval.ecommerce_api.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AdminOrderControllerTest {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User createUser(Role role, String name, String email){
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("encryptedPassword");
        user.setRole(role);
        return userRepository.save(user);
    }

    private String loginUserAndGenerateToken(Role role, String name, String email) {
        User user = createUser(role, name, email);
        return jwtUtils.generateToken(user.getEmail(), user.getRole().name());
    }

    private Category getCategory(String categoryName) {
        if (!categoryRepository.existsByName(categoryName)) {
            Category newCategory = new Category();
            newCategory.setName(categoryName);
            categoryRepository.save(newCategory);
        }
        return categoryRepository.findByName(categoryName).orElseThrow();
    }

    private Product createProduct(String category, String name, String price, Integer stock) {
        Product product = new Product();
        product.setCategory(getCategory(category));
        product.setName(name);
        product.setPrice(new BigDecimal(price));
        product.setStockQuantity(stock);
        return productRepository.save(product);
    }

    private void addToCart(Long productId, Integer qty, String token) throws Exception{
        CartRequest request = new CartRequest();
        request.setProductId(productId);
        request.setQuantity(qty);
        mockMvc.perform(post("/api/carts/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private String createOrder(Product product, int qty, String token) throws Exception{
        addToCart(product.getId(), qty,token);
        PlaceOrderRequest request = new PlaceOrderRequest("123 Mock Address");
        return mockMvc.perform(post("/api/orders/place")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void testGetAllOrders_AsAdmin_ShouldReturn200OkAndPageOfOrders() throws Exception {
        // ARRANGE
        String standardTokenA = loginUserAndGenerateToken(Role.ROLE_USER, "Standard User A", "user_A@test.com");
        Product product1 = createProduct("Electronics", "Pixel 10 Pro", "899.99", 100);
        String orderA = createOrder(product1, 2, standardTokenA);

        String standardTokenB = loginUserAndGenerateToken(Role.ROLE_USER, "Standard User B", "user_B@test.com");
        Product product2 = createProduct("Books", "Lord of the Rings", "14.99", 50);
        String orderB = createOrder(product2, 1, standardTokenB);

        String adminToken = loginUserAndGenerateToken(Role.ROLE_ADMIN, "Admin User", "admin@test.com");

        int orderA_ExpectedId = JsonPath.read(orderA, "$.id");
        int orderB_ExpectedId = JsonPath.read(orderB, "$.id");
        // ACT & ASSERT
        mockMvc.perform(get("/api/admin/orders")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2))
                // Second order is expected to be first due to Pageable default sort & direction
                .andExpect(jsonPath("$.content[0].userEmail").value(jwtUtils.extractUsername(standardTokenB)))
                .andExpect(jsonPath("$.content[0].id").value(orderB_ExpectedId))
                .andExpect(jsonPath("$.content[1].userEmail").value(jwtUtils.extractUsername(standardTokenA)))
                .andExpect(jsonPath("$.content[1].id").value(orderA_ExpectedId));
    }
}
