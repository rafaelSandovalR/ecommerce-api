package com.rsandoval.ecommerce_api.config;

import com.rsandoval.ecommerce_api.model.Category;
import com.rsandoval.ecommerce_api.model.Product;
import com.rsandoval.ecommerce_api.repository.CategoryRepository;
import com.rsandoval.ecommerce_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {

        String name = "Smart Watch";
        String description = "Track your fitness and notifications on the go.";
        BigDecimal price = new BigDecimal("249.99");
        String imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80";
        Integer stockQty = 10;
        Long categoryId  = 1L;


        if (!productRepository.existsByName(name)) {
            System.out.println("[DataSeeder] Seeding data...");

            Product p1 = new Product();
            Category category = categoryRepository.getReferenceById(categoryId);

            p1.setName(name);
            p1.setDescription(description);
            p1.setPrice(price);
            p1.setImageUrl(imageUrl);
            p1.setStockQuantity(stockQty);
            p1.setCategory(category);

            productRepository.save(p1);

            System.out.println("[DataSeeder] Data seeded successfully! Added " + name);
        }
    }
}
