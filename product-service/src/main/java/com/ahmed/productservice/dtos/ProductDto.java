package com.ahmed.productservice.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public final class ProductDto {

    public record Response(
            Long id,
            String name,
            String description,
            BigDecimal price,
            String sku,
            String imageUrl,
            CategoryDto.Response category
    ) {}

    public record Request(
            @NotBlank(message = "Product name is required")
            String name,

            String description,

            @NotNull(message = "Price is required")
            @Positive(message = "Price must be greater than zero")
            BigDecimal price,

            @NotBlank(message = "SKU is required")
            String sku,

            String imageUrl,

            @NotNull(message = "Category ID is required")
            Long categoryId
    ) {}
}