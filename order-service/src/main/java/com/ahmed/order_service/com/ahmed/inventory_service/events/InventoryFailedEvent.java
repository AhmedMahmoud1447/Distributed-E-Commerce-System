package com.ahmed.order_service.com.ahmed.inventory_service.events;

public record InventoryFailedEvent(
        Long orderId,
        String orderNumber,
        String reason
) {}