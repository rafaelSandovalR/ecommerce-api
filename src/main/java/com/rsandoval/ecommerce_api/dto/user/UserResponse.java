package com.rsandoval.ecommerce_api.dto.user;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String name;
}
