package com.ahmed.inventory_service.consumers;

import com.ahmed.inventory_service.events.PaymentProcessedEvent;
import com.ahmed.inventory_service.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentResponseConsumer {

    private final InventoryRepository inventoryRepository;

    @KafkaListener(topics = "payment-status-events", groupId = "inventory-group")
    @Transactional
    public void consumePaymentResponse(PaymentProcessedEvent event) {
        log.info("Inventory Service received PaymentProcessedEvent for order: {} with status: {}", event.orderNumber(), event.status());

        if (!"SUCCESS".equals(event.status()) && event.items() != null) {
            log.warn("Payment failed for order: {}. Executing full inventory rollback...", event.orderNumber());

            for (PaymentProcessedEvent.PaymentOrderItem item : event.items()) {
                inventoryRepository.findByProductId(item.productId()).ifPresent(inventory -> {
                    int updatedQuantity = inventory.getAvailableQuantity() + item.quantity();
                    inventory.setAvailableQuantity(updatedQuantity);
                    inventoryRepository.save(inventory);
                    log.info("Restored {} units for Product ID: {}. New Stock: {}", item.quantity(), item.productId(), updatedQuantity);
                });
            }
            log.info("Inventory rollback completed successfully for order: {}", event.orderNumber());
        }
    }
}