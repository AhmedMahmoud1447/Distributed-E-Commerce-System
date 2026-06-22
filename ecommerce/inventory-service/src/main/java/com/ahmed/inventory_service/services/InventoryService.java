package com.ahmed.inventory_service.services;

import com.ahmed.inventory_service.events.PaymentFailedEvent.InventoryStockItem;
import java.util.List;

public interface InventoryService {
    void releaseStock(List<InventoryStockItem> items);
}