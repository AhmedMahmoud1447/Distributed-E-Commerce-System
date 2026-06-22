package com.ahmed.order_service.producers;

import com.ahmed.order_service.events.OrderCreatedEvent;
import com.ahmed.order_service.events.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "order-created-events";

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Publishing OrderCreatedEvent for Order Number: {}", event.orderNumber());
        kafkaTemplate.send(TOPIC, event.orderNumber(), event);
    }

    public void sendOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("Sending OrderPlacedEvent for order: {}", event.orderNumber());
        kafkaTemplate.send("order-placed-events", event.orderNumber(), event);
    }
}