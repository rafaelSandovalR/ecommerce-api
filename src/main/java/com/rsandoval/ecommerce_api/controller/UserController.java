package com.rsandoval.ecommerce_api.controller;

import com.rsandoval.ecommerce_api.dto.UserRequest;
import com.rsandoval.ecommerce_api.model.User;
import com.rsandoval.ecommerce_api.model.UserResponse;
import com.rsandoval.ecommerce_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse createdUser = userService.createUser(request);
        return ResponseEntity
                .created( URI.create("/api/users/" + createdUser.getId()))
                .body(createdUser);
    }
}
