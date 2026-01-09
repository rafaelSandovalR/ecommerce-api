package com.rsandoval.ecommerce_api.repository;

import com.rsandoval.ecommerce_api.model.Order;
import com.rsandoval.ecommerce_api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser(User user, Pageable pageable);

}
