package com.rsandoval.ecommerce_api.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String name;
}
