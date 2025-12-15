package com.rsandoval.ecommerce_api.controller;

import com.rsandoval.ecommerce_api.model.Category;
import com.rsandoval.ecommerce_api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category savedCategory = categoryService.createCategory(category);
        return ResponseEntity
                .created(URI.create("/api/categories/" + savedCategory.getId()))
                .body(savedCategory);
    }
}
