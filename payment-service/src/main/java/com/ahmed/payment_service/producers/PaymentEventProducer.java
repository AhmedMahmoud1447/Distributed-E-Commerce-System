package com.ahmed.payment_service.producers;

import com.ahmed.payment_service.events.PaymentProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentProcessedEvent(PaymentProcessedEvent event) {
        log.info("Publishing PaymentProcessedEvent for order: {}", event.orderNumber());
        kafkaTemplate.send("payment-status-events", event.orderNumber(), event);
    }
}