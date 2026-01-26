package com.rsandoval.ecommerce_api.mapper;

import com.rsandoval.ecommerce_api.dto.user.UserRequest;
import com.rsandoval.ecommerce_api.enums.Role;
import com.rsandoval.ecommerce_api.model.User;
import com.rsandoval.ecommerce_api.dto.user.UserResponse;
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

    public User toEntity(UserRequest request, String encodedPassword) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(encodedPassword);
        user.setRole(Role.ROLE_USER);
        return user;
    }
}
