package com.ahmed.payment_service.events;

import java.math.BigDecimal;
import java.util.List;

public record OrderPlacedEvent(
        Long orderId,
        String orderNumber,
        Long customerId,
        BigDecimal totalPrice,
        List<OrderPlacedItem> items
) {
    public record OrderPlacedItem(Long productId, Integer quantity) {}
}