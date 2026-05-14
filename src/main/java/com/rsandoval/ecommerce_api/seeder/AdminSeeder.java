package com.rsandoval.ecommerce_api.seeder;

import com.rsandoval.ecommerce_api.enums.Role;
import com.rsandoval.ecommerce_api.model.User;
import com.rsandoval.ecommerce_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
            System.out.println("Admin User successfully seeded.");
        }
    }
}
