package com.ahmed.order_service.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public class OrderDto {

    public record Request(
            @NotNull(message = "Customer ID is required")
            Long customerId,

            @NotEmpty(message = "Order must contain at least one item")
            @Valid
            List<OrderItemRequest> items
    ) {}

    public record OrderItemRequest(
            @NotNull(message = "Product ID is required")
            Long productId,

            @NotNull(message = "Quantity is required")
            @Positive(message = "Quantity must be greater than zero")
            Integer quantity
    ) {}

    public record Response(
            Long id,
            String orderNumber,
            Long customerId,
            String status,
            BigDecimal totalAmount,
            List<OrderItemResponse> items
    ) {}

    public record OrderItemResponse(
            Long id,
            Long productId,
            BigDecimal price,
            Integer quantity
    ) {}
}