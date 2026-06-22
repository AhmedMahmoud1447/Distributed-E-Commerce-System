package com.ahmed.notification_service.consumers;

import com.ahmed.notification_service.events.PaymentFailedEvent;
import com.ahmed.notification_service.events.PaymentSuccessEvent;
import com.ahmed.notification_service.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;
    private static final String CUSTOMER_EMAIL = "ahmed.test@example.com";

    @KafkaListener(
            topics = "payment-events",
            groupId = "notification-group",
            properties = "spring.json.value.default.type=com.ahmed.notification_service.events.PaymentSuccessEvent"
    )
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("Processing PaymentSuccessEvent for Order ID: {}", event.orderId());

        String subject = "Order Confirmed! 🎉";
        String body = "Hello,\n\nPayment was successful for Order #" + event.orderId() + ".\nWe are preparing your shipment.";

        emailService.sendEmail(CUSTOMER_EMAIL, subject, body);
    }

    @KafkaListener(
            topics = "payment-events",
            groupId = "notification-group",
            properties = "spring.json.value.default.type=com.ahmed.notification_service.events.PaymentFailedEvent"
    )
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.warn("Processing PaymentFailedEvent for Order ID: {}", event.orderId());

        String subject = "Payment Failed ❌";
        String body = "Hello,\n\nPayment failed for Order #" + event.orderId() + " due to: " + event.reason() + ".\nYour order has been cancelled.";

        emailService.sendEmail(CUSTOMER_EMAIL, subject, body);
    }
}