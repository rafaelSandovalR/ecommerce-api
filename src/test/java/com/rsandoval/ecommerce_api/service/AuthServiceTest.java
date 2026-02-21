package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.dto.auth.AuthResponse;
import com.rsandoval.ecommerce_api.dto.auth.LoginRequest;
import com.rsandoval.ecommerce_api.dto.user.UserRequest;
import com.rsandoval.ecommerce_api.model.User;
import com.rsandoval.ecommerce_api.repository.UserRepository;
import com.rsandoval.ecommerce_api.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    @Test
    void testLogin_WithBadCredentials_ShouldThrowException() {
        // ARRANGE
        LoginRequest request = new LoginRequest();
        request.setEmail("wrong@email.com");
        request.setPassword("wrongpassword");

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // ACT & ASSERT
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(request);
        });

        verify(jwtUtils, never()).generateToken(anyString(), anyString());
    }

    @Test
    void testLogin_WithValidCredentials_ShouldReturnToken() {
        // ARRANGE
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("password");

        Authentication mockAuth = mock(Authentication.class);
        GrantedAuthority mockAuthority = mock(GrantedAuthority.class);

        when(mockAuthority.getAuthority()).thenReturn("ROLE_USER");
        doReturn(List.of(mockAuthority)).when(mockAuth).getAuthorities();
        when(mockAuth.getName()).thenReturn("user@test.com");
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
        when(jwtUtils.generateToken("user@test.com", "ROLE_USER")).thenReturn("fake-jwt-token");

        // ACT
        AuthResponse response = authService.login(request);

        // ASSERT
        assertNotNull(response);
        assertEquals("fake-jwt-token", response.token());
    }

    @Test
    void testRegister_WhenEmailAlreadyExists_ShouldThrowException() {
        // ARRANGE
        UserRequest request = new UserRequest();
        request.setEmail("Existing@Test.com"); // Test case sensitivity
        request.setPassword("password");

        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        // ACT
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(request);
        });

        // ASSERT
        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
