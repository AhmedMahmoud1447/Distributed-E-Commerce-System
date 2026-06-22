package com.ahmed.inventory_service.consumers;

import com.ahmed.inventory_service.com.ahmed.order_service.events.OrderCreatedEvent;
import com.ahmed.inventory_service.domain.Inventory;
import com.ahmed.inventory_service.events.InventoryFailedEvent;
import com.ahmed.inventory_service.events.InventoryReservedEvent;
import com.ahmed.inventory_service.producers.InventoryEventProducer;
import com.ahmed.inventory_service.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedConsumer {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventProducer inventoryEventProducer;

    @KafkaListener(topics = "order-created-events", groupId = "inventory-group")
    @Transactional
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent for Order Number: {}", event.orderNumber());

        List<Inventory> inventoriesToUpdate = new ArrayList<>();

        for (OrderCreatedEvent.OrderLineItemEvent item : event.items()) {
            Inventory inventory = inventoryRepository.findByProductId(item.productId())
                    .orElse(null);

            if (inventory == null) {
                inventoryEventProducer.sendFailedEvent(new InventoryFailedEvent(
                        event.orderId(), event.orderNumber(), "Product ID " + item.productId() + " not found in Inventory"
                ));
                return;
            }

            if (inventory.getAvailableQuantity() < item.quantity()) {
                inventoryEventProducer.sendFailedEvent(new InventoryFailedEvent(
                        event.orderId(), event.orderNumber(), "In-sufficient stock for Product ID: " + item.productId()
                ));
                return;
            }

            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - item.quantity());
            inventoriesToUpdate.add(inventory);
        }

        inventoryRepository.saveAll(inventoriesToUpdate);

        inventoryEventProducer.sendReservedEvent(new InventoryReservedEvent(
                event.orderId(), event.orderNumber()
        ));
    }
}