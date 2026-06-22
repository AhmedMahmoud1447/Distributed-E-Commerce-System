package com.ahmed.order_service.events;

import java.math.BigDecimal;

public record OrderPlacedEvent(
        Long orderId,
        String orderNumber,
        Long customerId,
        BigDecimal totalPrice
) {}