package com.rsandoval.ecommerce_api.repository;

import com.rsandoval.ecommerce_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
