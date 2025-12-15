package com.rsandoval.ecommerce_api.repository;

import com.rsandoval.ecommerce_api.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);
}
