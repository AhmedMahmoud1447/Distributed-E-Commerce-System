package com.ahmed.payment_service.consumers;

import com.ahmed.payment_service.events.OrderPlacedEvent;
import com.ahmed.payment_service.events.PaymentProcessedEvent;
import com.ahmed.payment_service.producers.PaymentEventProducer;
import com.ahmed.payment_service.repositories.UserBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPlacedConsumer {

    private final UserBalanceRepository userBalanceRepository;
    private final PaymentEventProducer paymentEventProducer;

    @KafkaListener(topics = "order-placed-events", groupId = "payment-group")
    @Transactional
    public void consumeOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for order: {}", event.orderNumber());

        List<PaymentProcessedEvent.PaymentOrderItem> paymentItems = event.items().stream()
                .map(item -> new PaymentProcessedEvent.PaymentOrderItem(item.productId(), item.quantity()))
                .toList();

        userBalanceRepository.findByCustomerId(event.customerId())
                .ifPresentOrElse(userBalance -> {
                    if (userBalance.getBalance().compareTo(event.totalPrice()) >= 0) {
                        userBalance.setBalance(userBalance.getBalance().subtract(event.totalPrice()));
                        userBalanceRepository.save(userBalance);
                        log.info("Payment successful for order: {}. New balance: {}", event.orderNumber(),
                                userBalance.getBalance());

                        paymentEventProducer.sendPaymentProcessedEvent(new PaymentProcessedEvent(
                                event.orderId(),
                                event.orderNumber(),
                                "SUCCESS",
                                paymentItems
                        ));
                    } else {
                        log.warn("Payment failed for order: {}. Insufficient funds.", event.orderNumber());

                        paymentEventProducer.sendPaymentProcessedEvent(new PaymentProcessedEvent(
                                event.orderId(),
                                event.orderNumber(),
                                "FAILED_INSUFFICIENT_FUNDS",
                                paymentItems
                        ));
                    }
                }, () -> {
                    log.error("Payment failed for order: {}. Customer ID {} not found.", event.orderNumber(),
                            event.customerId());

                    paymentEventProducer.sendPaymentProcessedEvent(new PaymentProcessedEvent(
                            event.orderId(),
                            event.orderNumber(),
                            "FAILED_CUSTOMER_NOT_FOUND",
                            paymentItems
                    ));
                });
    }
}