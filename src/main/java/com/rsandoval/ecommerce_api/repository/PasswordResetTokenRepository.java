package com.rsandoval.ecommerce_api.repository;

import com.rsandoval.ecommerce_api.model.PasswordResetToken;
import com.rsandoval.ecommerce_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);
}
