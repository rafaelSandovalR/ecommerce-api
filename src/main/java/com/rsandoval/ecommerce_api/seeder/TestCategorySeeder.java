package com.rsandoval.ecommerce_api.seeder;

import com.rsandoval.ecommerce_api.model.Category;
import com.rsandoval.ecommerce_api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Profile("test")
public class TestCategorySeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {

        Optional<Category> existingCategory = categoryRepository.findByName("Admin Category");
        if (existingCategory.isEmpty()) {
            Category category = new Category();
            category.setName("Admin Category");
            categoryRepository.save(category);
            System.out.println("E2E Test Category successfully seeded.");
        }
    }
}
