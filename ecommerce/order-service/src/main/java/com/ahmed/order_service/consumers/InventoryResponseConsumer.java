package com.ahmed.order_service.consumers;

import com.ahmed.order_service.com.ahmed.inventory_service.events.InventoryFailedEvent;
import com.ahmed.order_service.com.ahmed.inventory_service.events.InventoryReservedEvent;
import com.ahmed.order_service.domain.Order;
import com.ahmed.order_service.domain.OrderStatus;
import com.ahmed.order_service.events.OrderPlacedEvent;
import com.ahmed.order_service.producers.OrderEventProducer;
import com.ahmed.order_service.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryResponseConsumer {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    @KafkaListener(topics = "inventory-status-events", groupId = "order-group")
    @Transactional
    public void consumeInventoryResponse(Object event) {
        if (event instanceof InventoryReservedEvent reservedEvent) {
            log.info("Order {} successfully reserved in inventory. Updating status to PLACED.", reservedEvent.orderNumber());

            orderRepository.findById(reservedEvent.orderId()).ifPresent(order -> {
                order.setStatus(OrderStatus.PLACED);
                orderRepository.save(order);

                orderEventProducer.sendOrderPlacedEvent(new OrderPlacedEvent(
                        order.getId(),
                        order.getOrderNumber(),
                        order.getCustomerId(),
                        order.getTotalAmount()
                ));
            });
        }
        else if (event instanceof InventoryFailedEvent failedEvent) {
            log.warn("Order {} failed in inventory due to: {}. Rolling back status to FAILED.",
                    failedEvent.orderNumber(), failedEvent.reason());

            orderRepository.findById(failedEvent.orderId()).ifPresent(order -> {
                order.setStatus(OrderStatus.FAILED);
                orderRepository.save(order);
            });
        }
    }
}