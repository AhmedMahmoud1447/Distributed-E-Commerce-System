package com.ahmed.productservice.dtos;

import jakarta.validation.constraints.NotBlank;

public final class CategoryDto {

    public record Response(
            Long id,
            String name,
            String description
    ) {}

    public record Request(
            @NotBlank(message = "Category name is required")
            String name,
            String description
    ) {}
}