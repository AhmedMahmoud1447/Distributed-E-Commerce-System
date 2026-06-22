package com.ahmed.productservice.controllers;

import com.ahmed.productservice.dtos.CategoryDto;
import com.ahmed.productservice.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto.Response> createCategory(
            @Valid @RequestBody CategoryDto.Request request) {
        CategoryDto.Response createdCategory = categoryService.createCategory(request);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto.Response> getCategoryById(@PathVariable Long id) {
        CategoryDto.Response category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto.Response>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}