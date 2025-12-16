package com.rsandoval.ecommerce_api.repository;

import com.rsandoval.ecommerce_api.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
