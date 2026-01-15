package com.rsandoval.ecommerce_api.dto.auth;

public record AuthResponse(String token, Long id, String email) {
}
