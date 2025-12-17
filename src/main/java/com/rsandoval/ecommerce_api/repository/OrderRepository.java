package com.rsandoval.ecommerce_api.repository;

import com.rsandoval.ecommerce_api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}
