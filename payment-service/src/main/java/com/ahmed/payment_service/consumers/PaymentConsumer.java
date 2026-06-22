package com.ahmed.payment_service.consumers;

import com.ahmed.payment_service.events.OrderPlacedEvent;
import com.ahmed.payment_service.events.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-events", groupId = "payment-group")
    public void consumeOrderEvent(OrderPlacedEvent event) {
        log.info("Received order event in Payment Service for Order ID: {}", event.orderId());

        try {
            boolean isPaymentSuccessful = checkAndProcessPayment(event);

            if (isPaymentSuccessful) {
                log.info("Payment successful for Order ID: {}", event.orderId());
            } else {
                log.warn("Payment failed due to insufficient funds for Order ID: {}", event.orderId());
                kafkaTemplate.send("payment-events", new PaymentFailedEvent(
                        event.orderId(),
                        event.items(),
                        "INSUFFICIENT_FUNDS"
                ));
            }
        } catch (Exception e) {
            log.error("Error processing payment for Order ID: {}", event.orderId(), e);
            kafkaTemplate.send("payment-events", new PaymentFailedEvent(
                    event.orderId(),
                    event.items(),
                    "SYSTEM_ERROR"
            ));
        }
    }

    private boolean checkAndProcessPayment(OrderPlacedEvent event) {
        return event.totalPrice().doubleValue() < 1000.0;
    }
}