package com.ahmed.inventory_service.consumers;

import com.ahmed.inventory_service.events.PaymentFailedEvent;
import com.ahmed.inventory_service.services.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentFailedConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(topics = "payment-events", groupId = "inventory-payment-failed-group")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.warn("Received PaymentFailedEvent for Order ID: {}. Releasing stock for all items.", event.orderId());
        inventoryService.releaseStock(event.items());
    }
}