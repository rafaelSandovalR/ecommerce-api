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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository; // Added to save test data directly

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User createStandardUser() {
        User user = new User();
        user.setName("Standard User");
        user.setEmail("user@test.com");
        user.setPassword("encryptedPassword");
        user.setRole(Role.ROLE_USER);
        return userRepository.save(user);
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

        // Link item to order
        order.setItems(List.of(item));

        return orderRepository.save(order);
    }

    @Test
    void testGetUserOrders_ShouldReturn200OkAndPageOfOrders() throws Exception {
        // ARRANGE
        User user = createStandardUser();
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());

        Product product = createProduct("Books", "Pride & Prejudice", "9.99", 25);
        createOrderDirectlyInDatabase(user, product, 2);

        // ACT & ASSERT
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].items[0].productName").value(product.getName()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetOrder_ShouldReturn200OkAndOrderDetails() throws Exception {
        // ARRANGE
        User user = createStandardUser();
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());

        Product product = createProduct("Office", "Stapler", "7.99", 30);
        Order order = createOrderDirectlyInDatabase(user, product, 3);

        // ACT & ASSERT
        mockMvc.perform(get("/api/orders/" + order.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.totalPrice").value(order.getTotalPrice().doubleValue()))
                .andExpect(jsonPath("$.items[0].productName").value(product.getName()));
    }
}