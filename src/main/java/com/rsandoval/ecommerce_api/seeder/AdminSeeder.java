package com.rsandoval.ecommerce_api.seeder;

import com.rsandoval.ecommerce_api.enums.Role;
import com.rsandoval.ecommerce_api.model.Category;
import com.rsandoval.ecommerce_api.model.User;
import com.rsandoval.ecommerce_api.repository.CategoryRepository;
import com.rsandoval.ecommerce_api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("test")
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Optional<User> existingAdmin = userRepository.findByEmail("admin@test.com");

        if (existingAdmin.isEmpty()) {
            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail("admin@test.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);
            userRepository.save(admin);
            System.out.println("E2E Test Admin User successfully seeded.");
        }

        Optional<Category> existingCategory = categoryRepository.findByName("Admin Category");
        if (existingCategory.isEmpty()) {
            Category category = new Category();
            category.setName("Admin Category");
            categoryRepository.save(category);
            System.out.println("E2E Test Category successfully seeded.");
        }
    }
}
