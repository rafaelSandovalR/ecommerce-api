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

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getUserCart());
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addItemToCart(@Valid @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(request));
    }

    @PutMapping("/items")
    public ResponseEntity<CartResponse> updateItemQuantity(@Valid @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(request));
    }

    // DELETE /api/carts/remove/5 (where 5 is the cartItemId, not productId)
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(@PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(itemId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}
