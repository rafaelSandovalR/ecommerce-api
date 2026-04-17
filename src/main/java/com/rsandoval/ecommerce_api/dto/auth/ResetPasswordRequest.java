package com.rsandoval.ecommerce_api.dto.auth;

public record ResetPasswordRequest(String token, String newPassword) {
}
