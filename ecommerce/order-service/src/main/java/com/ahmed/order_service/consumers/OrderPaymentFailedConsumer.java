package com.ahmed.order_service.consumers;

import com.ahmed.order_service.events.PaymentFailedEvent;
import com.ahmed.order_service.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPaymentFailedConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "payment-events", groupId = "order-payment-failed-group")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.warn("Received PaymentFailedEvent for Order ID: {}, Reason: {}", event.orderId(), event.reason());
        orderService.cancelOrder(event.orderId());
    }
}