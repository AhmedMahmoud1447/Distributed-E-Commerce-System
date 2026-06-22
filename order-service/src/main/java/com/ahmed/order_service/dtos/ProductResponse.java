package com.ahmed.order_service.dtos;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String sku,
        BigDecimal price
) {}