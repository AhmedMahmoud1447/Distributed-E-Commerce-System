package com.ahmed.inventory_service.com.ahmed.order_service.events;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        String orderNumber,
        Long customerId,
        BigDecimal totalAmount,
        List<OrderLineItemEvent> items
) {
    public record OrderLineItemEvent(
            Long productId,
            Integer quantity
    ) {}
}