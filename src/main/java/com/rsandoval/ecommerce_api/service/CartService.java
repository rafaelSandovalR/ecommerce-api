package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.dto.CartItemDto;
import com.rsandoval.ecommerce_api.dto.CartRequest;
import com.rsandoval.ecommerce_api.dto.CartResponse;
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
    private final CartMapper cartMapper;

    public CartResponse getCartByUserId(Long userId) {
        // Find existing cart or create a new one if none exists
        Cart cart = getCartEntity(userId);
        return cartMapper.toDTO(cart);
    }

    private Cart createCartForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID:" + userId));

        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    @Transactional // Ensures all steps happen or non happen
    public CartResponse addItemToCart(Long userId, CartRequest request) {
        Long productId = request.getProductId();
        Integer qty = request.getQuantity();

        Cart cart = getCartEntity(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        // Logic: Check if product already exists in the cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        int currentQtyInCart = existingItem.map(CartItem::getQuantity).orElse(0);
        int newTotalQty = currentQtyInCart + qty;
        // Check against inventory
        if (newTotalQty > product.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient stock. You are requesting " + newTotalQty + " in cart. Product stock is " + product.getStockQuantity()
            );
        }

        if (existingItem.isPresent()) {
            // Scenario A: Item exists, just update qty
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + qty);
        } else {
            // Scenario B: Item is new, create it and add to cart
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(qty);
            newItem.setPrice(product.getPrice()); // Snapshot the price
            cart.getItems().add(newItem);
        }

        updateCartTotal(cart);
        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.toDTO(updatedCart);
    }

    @Transactional
    public CartResponse updateItemQuantity(Long userId, CartRequest request) {
        Long productId = request.getProductId();
        Integer qty = request.getQuantity();
        Cart cart = getCartEntity(userId);

        CartItem item = cart.getItems()
                .stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with ID: " + productId + " not found in cart"
                ));

        Product product = item.getProduct();

        if (qty > product.getStockQuantity()) {
            throw new IllegalArgumentException("Insufficient stock. Product stock is " + product.getStockQuantity());
        }

        item.setQuantity(qty);
        item.setPrice(product.getPrice());

        updateCartTotal(cart);
        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.toDTO(updatedCart);
    }

    @Transactional
    public CartResponse removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = getCartEntity(userId);

        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        if (!removed){
            throw new ResourceNotFoundException("CartItem not found with ID: " + cartItemId);
        }

        updateCartTotal(cart);
        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.toDTO(updatedCart);
    }

    public void clearCart(Long userId) {
        Cart cart = getCartEntity(userId);
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

    public Cart getCartEntity(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createCartForUser(userId));
    }
}
