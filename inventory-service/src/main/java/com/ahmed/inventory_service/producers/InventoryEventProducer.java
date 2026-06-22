package com.ahmed.inventory_service.producers;

import com.ahmed.inventory_service.events.InventoryFailedEvent;
import com.ahmed.inventory_service.events.InventoryReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendReservedEvent(InventoryReservedEvent event) {
        log.info("Publishing InventoryReservedEvent for Order: {}", event.orderNumber());
        kafkaTemplate.send("inventory-status-events", event.orderNumber(), event);
    }

    public void sendFailedEvent(InventoryFailedEvent event) {
        log.error("Publishing InventoryFailedEvent for Order: {}. Reason: {}", event.orderNumber(), event.reason());
        kafkaTemplate.send("inventory-status-events", event.orderNumber(), event);
    }
}