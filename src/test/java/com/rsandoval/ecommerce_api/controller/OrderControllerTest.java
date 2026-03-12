package com.rsandoval.ecommerce_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsandoval.ecommerce_api.dto.cart.CartRequest;
import com.rsandoval.ecommerce_api.dto.order.OrderResponse;
import com.rsandoval.ecommerce_api.dto.order.PlaceOrderRequest;
import com.rsandoval.ecommerce_api.enums.OrderStatus;
import com.rsandoval.ecommerce_api.enums.Role;
import com.rsandoval.ecommerce_api.model.Category;
import com.rsandoval.ecommerce_api.model.Product;
import com.rsandoval.ecommerce_api.model.User;
import com.rsandoval.ecommerce_api.repository.CategoryRepository;
import com.rsandoval.ecommerce_api.repository.ProductRepository;
import com.rsandoval.ecommerce_api.repository.UserRepository;
import com.rsandoval.ecommerce_api.security.JwtUtils;
import com.rsandoval.ecommerce_api.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User createStandardUser() {
        User user = new User();
        user.setName("Standard User");
        user.setEmail("user@test.com");
        user.setPassword("encryptedPassword");
        user.setRole(Role.ROLE_USER);
        return userRepository.save(user);
    }

    private String loginAndGenerateToken() {
        User user = createStandardUser();
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

    private void createOrder(Product product, int qty, String token) throws Exception{
        addToCart(product.getId(), qty,token);
        PlaceOrderRequest request = new PlaceOrderRequest("123 Mock Address");
        mockMvc.perform(post("/api/orders/place")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testPlaceOrder_ShouldReturn201CreatedAndPlacedOrder() throws Exception {
        // ARRANGE
        String token = loginAndGenerateToken();
        Product product = createProduct("Clothing", "T-shirt", "34.95", 20);
        int cartQty = 3;
        addToCart(product.getId(), cartQty, token);

        PlaceOrderRequest request = new PlaceOrderRequest("123 Mock Address");
        double expectedTotal = product.getPrice().multiply(BigDecimal.valueOf(cartQty)).doubleValue();

        // ACT & ASSERT
        mockMvc.perform(post("/api/orders/place")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.shippingAddress").value(request.shippingAddress()))
                .andExpect(jsonPath("$.totalPrice").value(expectedTotal))
                .andExpect(jsonPath("$.items[0].quantity").value(cartQty))
                .andExpect(jsonPath("$.items[0].productName").value(product.getName()));
    }

    @Test
    void testGetUserOrders_ShouldReturn200OkAndPageOfOrders() throws Exception {
        // ARRANGE
        String token = loginAndGenerateToken();
        Product product = createProduct("Books", "Pride & Prejudice", "9.99", 25);
        createOrder(product, 2, token);

        // ACT & ASSERT
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].items[0].productName").value(product.getName()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
