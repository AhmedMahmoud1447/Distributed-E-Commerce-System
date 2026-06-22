package com.ahmed.inventory_service.events;

public record InventoryFailedEvent(
        Long orderId,
        String orderNumber,
        String reason
) {}