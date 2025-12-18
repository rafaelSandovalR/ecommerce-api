package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.dto.UserRequest;
import com.rsandoval.ecommerce_api.model.User;
import com.rsandoval.ecommerce_api.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toDTO(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public User toEntity(UserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return user;
    }
}
