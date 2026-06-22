package com.ahmed.inventory_service.events;

import java.util.List;

public record PaymentFailedEvent(
        Long orderId,
        List<InventoryStockItem> items,
        String reason
) {
    public record InventoryStockItem(Long productId, Integer quantity) {}
}