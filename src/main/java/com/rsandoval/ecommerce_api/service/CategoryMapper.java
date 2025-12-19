package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.dto.CategoryRequest;
import com.rsandoval.ecommerce_api.dto.CategoryResponse;
import com.rsandoval.ecommerce_api.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toDTO(Category category) {
        CategoryResponse dto = new CategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    public Category toEntity(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        return category;
    }
}
