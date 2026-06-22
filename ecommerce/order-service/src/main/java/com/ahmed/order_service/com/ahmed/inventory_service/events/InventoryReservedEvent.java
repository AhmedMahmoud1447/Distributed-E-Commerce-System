package com.ahmed.order_service.com.ahmed.inventory_service.events;

public record InventoryReservedEvent(
        Long orderId,
        String orderNumber
) {}