package com.ahmed.order_service.events;

public record PaymentFailedEvent(Long orderId, String reason) {}