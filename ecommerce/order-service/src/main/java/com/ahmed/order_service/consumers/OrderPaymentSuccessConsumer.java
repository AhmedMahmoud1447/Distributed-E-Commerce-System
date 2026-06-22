package com.ahmed.order_service.consumers;

import com.ahmed.order_service.events.PaymentSuccessEvent;
import com.ahmed.order_service.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPaymentSuccessConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "payment-events", groupId = "order-payment-success-group")
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("Received PaymentSuccessEvent for Order ID: {}", event.orderId());
        orderService.completeOrder(event.orderId());
    }
}