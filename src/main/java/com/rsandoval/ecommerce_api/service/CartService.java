package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.exception.ResourceNotFoundException;
import com.rsandoval.ecommerce_api.model.Cart;
import com.rsandoval.ecommerce_api.model.CartItem;
import com.rsandoval.ecommerce_api.model.Product;
import com.rsandoval.ecommerce_api.model.User;
import com.rsandoval.ecommerce_api.repository.CartRepository;
import com.rsandoval.ecommerce_api.repository.ProductRepository;
import com.rsandoval.ecommerce_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public Cart getCartByUserId(Long userId) {
        // Find existing cart or create a new one if none exists
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createCartForUser(userId));
    }

    private Cart createCartForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID:" + userId));

        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    @Transactional // Ensures all steps happen or non happen
    public Cart addItemToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getCartByUserId(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        // Logic: Check if product already exists in the cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Scenario A: Item exists, just update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // Scenario B: Item is new, create it and add to cart
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setPrice(product.getPrice()); // Snapshot the price
            cart.getItems().add(newItem);
        }

        updateCartTotal(cart);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = getCartByUserId(userId);

        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        if (!removed){
            throw new ResourceNotFoundException("CartItem not found with ID: " + cartItemId);
        }

        updateCartTotal(cart);
        return cartRepository.save(cart);
    }

    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().clear(); // orphanRemoval deletes them all from DB
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    private void updateCartTotal(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(item -> {
                    BigDecimal price = item.getPrice();
                    BigDecimal qty = BigDecimal.valueOf(item.getQuantity());
                    return price.multiply(qty);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(total);
    }
}
