package com.ahmed.inventory_service.services;

import com.ahmed.inventory_service.events.PaymentFailedEvent;
import com.ahmed.inventory_service.repositories.InventoryRepository;
import com.ahmed.inventory_service.services.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public void releaseStock(List<PaymentFailedEvent.InventoryStockItem> items) {
        items.forEach(item -> {
            log.info("Restoring stock for Product ID: {} with quantity: {}", item.productId(), item.quantity());

            inventoryRepository.findByProductId(item.productId()).ifPresent(inventory -> {
                inventory.setAvailableQuantity(inventory.getAvailableQuantity() + item.quantity());
                inventoryRepository.save(inventory);
            });
        });
    }
}