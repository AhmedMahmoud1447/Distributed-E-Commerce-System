package com.ahmed.payment_service.events;

import java.util.List;

public record PaymentProcessedEvent(
        Long orderId,
        String orderNumber,
        String status,
        List<PaymentOrderItem> items
) {
    public record PaymentOrderItem(Long productId, Integer quantity) {}
}