package com.ahmed.productservice.services;

import com.ahmed.productservice.dtos.CategoryDto;
import java.util.List;

public interface CategoryService {
    CategoryDto.Response createCategory(CategoryDto.Request request);
    CategoryDto.Response getCategoryById(Long id);
    List<CategoryDto.Response> getAllCategories();
}