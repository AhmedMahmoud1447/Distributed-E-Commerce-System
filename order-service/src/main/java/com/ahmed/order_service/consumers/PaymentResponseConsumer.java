package com.ahmed.order_service.consumers;

import com.ahmed.order_service.domain.OrderStatus;
import com.ahmed.order_service.events.PaymentProcessedEvent;
import com.ahmed.order_service.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentResponseConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "payment-status-events", groupId = "order-group")
    @Transactional
    public void consumePaymentResponse(PaymentProcessedEvent event) {
        log.info("Received PaymentProcessedEvent for order: {} with status: {}", event.orderNumber(), event.status());

        if ("SUCCESS".equals(event.status())) {
            orderRepository.findById(event.orderId()).ifPresent(order -> {
                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
                log.info("Order {} is now CONFIRMED. Saga Happy Path completed successfully!", order.getOrderNumber());
            });
        } else {
            orderRepository.findById(event.orderId()).ifPresent(order -> {
                order.setStatus(OrderStatus.FAILED);
                orderRepository.save(order);
                log.warn("Order {} failed due to payment issue. Status updated to FAILED.", order.getOrderNumber());
            });
        }
    }
}