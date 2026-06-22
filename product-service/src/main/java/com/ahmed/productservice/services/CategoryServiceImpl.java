package com.ahmed.productservice.services;

import com.ahmed.productservice.domain.Category;
import com.ahmed.productservice.dtos.CategoryDto;
import com.ahmed.productservice.error.exceptions.ResourceNotFoundException;
import com.ahmed.productservice.repositories.CategoryRepository;
import com.ahmed.productservice.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto.Response createCategory(CategoryDto.Request request) {
        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());

        Category savedCategory = categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto.Response getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        return mapToResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto.Response> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CategoryDto.Response mapToResponse(Category category) {
        return new CategoryDto.Response(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}