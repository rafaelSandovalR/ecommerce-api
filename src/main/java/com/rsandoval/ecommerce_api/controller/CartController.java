package com.rsandoval.ecommerce_api.controller;

import com.rsandoval.ecommerce_api.dto.cart.CartRequest;
import com.rsandoval.ecommerce_api.dto.cart.CartResponse;
import com.rsandoval.ecommerce_api.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<CartResponse> addItemToCart(@PathVariable Long userId, @Valid @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(userId, request));
    }

    @PutMapping("/{userId}/items")
    public ResponseEntity<CartResponse> updateItemQuantity(@PathVariable Long userId, @Valid @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, request));
    }

    // DELETE /api/carts/1/remove/5 (where 5 is the cartItemId, not productId)
    @DeleteMapping("/{userId}/remove/{itemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(@PathVariable Long userId, @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(userId, itemId));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
