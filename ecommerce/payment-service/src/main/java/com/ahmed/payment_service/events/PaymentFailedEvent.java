package com.ahmed.payment_service.events;


import java.util.List;

public record PaymentFailedEvent(
        Long orderId,
        List<OrderPlacedEvent.OrderPlacedItem> items,
        String reason
) {}