package com.rsandoval.ecommerce_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsandoval.ecommerce_api.enums.OrderStatus;
import com.rsandoval.ecommerce_api.enums.Role;
import com.rsandoval.ecommerce_api.model.Category;
import com.rsandoval.ecommerce_api.model.Order;
import com.rsandoval.ecommerce_api.model.OrderItem;
import com.rsandoval.ecommerce_api.model.Product;
import com.rsandoval.ecommerce_api.model.User;
import com.rsandoval.ecommerce_api.repository.CategoryRepository;
import com.rsandoval.ecommerce_api.repository.OrderRepository;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    OrderRepository orderRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User createUser(Role role, String name, String email){
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword("encryptedPassword");
            user.setRole(role);
            return userRepository.save(user);
        });
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

    private Order createOrderDirectlyInDatabase(User user, Product product, int qty) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress("123 Mock Address");
        order.setStatus(OrderStatus.PAID);
        order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(qty)));

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setPrice(product.getPrice());
        item.setQuantity(qty);

        order.setItems(List.of(item));

        return orderRepository.save(order);
    }

    @Test
    void testGetAllOrders_AsAdmin_ShouldReturn200OkAndPageOfOrders() throws Exception {
        // ARRANGE
        User userA = createUser(Role.ROLE_USER, "Standard User A", "user_A@test.com");
        Product product1 = createProduct("Electronics", "Pixel 10 Pro", "899.99", 100);
        Order orderA = createOrderDirectlyInDatabase(userA, product1, 2);

        User userB = createUser(Role.ROLE_USER, "Standard User B", "user_B@test.com");
        Product product2 = createProduct("Books", "Lord of the Rings", "14.99", 50);
        Order orderB = createOrderDirectlyInDatabase(userB, product2, 1);

        String adminToken = loginUserAndGenerateToken(Role.ROLE_ADMIN, "Admin User", "admin@test.com");

        // ACT & ASSERT
        mockMvc.perform(get("/api/admin/orders")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2))
                // Second order is expected to be first due to Pageable default sort & direction
                .andExpect(jsonPath("$.content[0].userEmail").value(userB.getEmail()))
                .andExpect(jsonPath("$.content[0].id").value(orderB.getId()))
                .andExpect(jsonPath("$.content[1].userEmail").value(userA.getEmail()))
                .andExpect(jsonPath("$.content[1].id").value(orderA.getId()));
    }

    @Test
    void testUpdateStatus_AsAdmin_ShouldReturn200OkAndUpdatedOrder() throws Exception {
        // ARRANGE
        User user = createUser(Role.ROLE_USER, "Standard User", "user@test.com");
        Product product = createProduct("Gaming", "GTA VI", "69.99", 100);
        Order order = createOrderDirectlyInDatabase(user, product, 3);

        assertEquals(OrderStatus.PAID, order.getStatus());

        String adminToken = loginUserAndGenerateToken(Role.ROLE_ADMIN, "Admin User", "admin@test.com");
        Map<String, String> requestPayload = Map.of("status", "SHIPPED");

        // ACT & ASSERT
        mockMvc.perform(put("/api/admin/orders/" + order.getId() + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(requestPayload.get("status")));
    }
}