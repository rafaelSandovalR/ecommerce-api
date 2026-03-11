package com.rsandoval.ecommerce_api.controller;

import com.jayway.jsonpath.JsonPath;
import com.rsandoval.ecommerce_api.dto.cart.CartRequest;
import com.rsandoval.ecommerce_api.dto.cart.CartResponse;
import com.rsandoval.ecommerce_api.enums.Role;
import com.rsandoval.ecommerce_api.model.Cart;
import com.rsandoval.ecommerce_api.model.Category;
import com.rsandoval.ecommerce_api.model.Product;
import com.rsandoval.ecommerce_api.model.User;
import com.rsandoval.ecommerce_api.repository.CartRepository;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User createStandardUser() {
        User user = new User();
        user.setEmail("user@test.com");
        user.setName("Standard User");
        user.setPassword("encryptedMockPassword");
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
        return user;
    }

    private Category createCategory(String categoryName) {
        Category category = new Category();
        category.setName(categoryName);
        return categoryRepository.save(category);
    }

    private Product createProduct(String category, String name, String price, Integer stock) {
        if (!categoryRepository.existsByName(category)) {
            Category newCategory = createCategory(category);
        }

        Category existingCategory = categoryRepository.findByName(category).orElseThrow();
        Product product = new Product();
        product.setCategory(existingCategory);
        product.setName(name);
        product.setPrice(new BigDecimal(price));
        product.setStockQuantity(stock);
        return productRepository.save(product);
    }

    private String loginUserAndGenerateToken() {
        User user = createStandardUser();
        return jwtUtils.generateToken(user.getEmail(), user.getRole().name());
    }

    @Test
    void testGetCart_ShouldReturn200OkAndEmptyCart() throws Exception {
        // ARRANGE
        String token = loginUserAndGenerateToken();

        // ACT & ASSERT
        mockMvc.perform(get("/api/carts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.totalPrice").value(0));
    }

    @Test
    void testAddItemToCart_ShouldReturn200OkAndUpdatedCart() throws Exception {
        // ARRANGE
        String token = loginUserAndGenerateToken();
        Product product = createProduct("Electronics", "Headphones", "119.99", 25);
        CartRequest request = new CartRequest();
        request.setProductId(product.getId());
        request.setQuantity(2);


        // ACT & ASSERT
        mockMvc.perform(post("/api/carts/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].productName").value(product.getName()))
                .andExpect(jsonPath("$.totalPrice").value(product.getPrice().doubleValue() * request.getQuantity()));
    }

    @Test
    void testUpdateItemQuantity_ShouldReturn200Ok() throws Exception {
        // ARRANGE
        String token = loginUserAndGenerateToken();
        Product product = createProduct("Clothing", "T-Shirt", "29.99", 30);
        CartRequest prepCart = new CartRequest();
        prepCart.setProductId(product.getId());
        prepCart.setQuantity(4);
        mockMvc.perform(post("/api/carts/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prepCart)))
                .andExpect(status().isOk());

        CartRequest request = new CartRequest();
        request.setProductId(product.getId());
        request.setQuantity(10);
        // ACT & ASSERT
        mockMvc.perform(put("/api/carts/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(request.getQuantity()))
                .andExpect(jsonPath("$.totalPrice").value(product.getPrice().doubleValue() * request.getQuantity()));
    }

    @Test
    void testRemoveItemFromCart_ShouldReturn200Ok() throws Exception {
        // ARRANGE
        String token = loginUserAndGenerateToken();
        Product product = createProduct("Kitchen", "Coffee Mug", "9.99", 50);
        CartRequest prepCart = new CartRequest();
        prepCart.setProductId(product.getId());
        prepCart.setQuantity(5);
        MvcResult result = mockMvc.perform(post("/api/carts/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prepCart)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Integer extractedId = JsonPath.read(jsonResponse, "$.items[0].id");
        long cartItemId = extractedId.longValue();

        // ACT & ASSERT
        mockMvc.perform(delete("/api/carts/remove/" + cartItemId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void testClearCart_ShouldReturn204NoContent() throws Exception {
        // ARRANGE
        User user = createStandardUser();
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());
        Product product = createProduct("Footwear", "Hoka Slides", "49.99", 10);
        CartRequest prepCart = new CartRequest();
        prepCart.setProductId(product.getId());
        prepCart.setQuantity(3);
        mockMvc.perform(post("/api/carts/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prepCart)))
                .andExpect(status().isOk());

        // ACT & ASSERT
        mockMvc.perform(delete("/api/carts/clear")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        Cart cart = cartRepository.findByUser(user).orElseThrow();
        assertTrue(cart.getItems().isEmpty());
    }
}
