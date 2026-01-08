package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.dto.AuthResponse;
import com.rsandoval.ecommerce_api.dto.LoginRequest;
import com.rsandoval.ecommerce_api.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = jwtUtils.generateToken(authentication.getName());
        return new AuthResponse(token);
    }
}
