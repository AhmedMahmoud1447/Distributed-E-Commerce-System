package com.ahmed.order_service.services;

import com.ahmed.order_service.client.ProductClient;
import com.ahmed.order_service.domain.Order;
import com.ahmed.order_service.domain.OrderItem;
import com.ahmed.order_service.domain.OrderStatus;
import com.ahmed.order_service.dtos.OrderDto;
import com.ahmed.order_service.dtos.ProductResponse;
import com.ahmed.order_service.events.OrderCreatedEvent;
import com.ahmed.order_service.producers.OrderEventProducer;
import com.ahmed.order_service.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final ProductClient productClient;

    @Override
    @Transactional
    public OrderDto.Response createOrder(OrderDto.Request request) {
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .customerId(request.customerId())
                .status(OrderStatus.PENDING)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderDto.OrderItemRequest itemRequest : request.items()) {
            ProductResponse productResponse = productClient.getProductById(itemRequest.productId());
            BigDecimal realPrice = productResponse.price();

            OrderItem orderItem = OrderItem.builder()
                    .productId(itemRequest.productId())
                    .quantity(itemRequest.quantity())
                    .price(realPrice)
                    .build();

            order.addOrderItem(orderItem);

            BigDecimal itemTotal = realPrice.multiply(BigDecimal.valueOf(itemRequest.quantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        List<OrderCreatedEvent.OrderLineItemEvent> eventItems = savedOrder.getItems().stream()
                .map(item -> new OrderCreatedEvent.OrderLineItemEvent(
                        item.getProductId(),
                        item.getQuantity()
                )).toList();

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getOrderNumber(),
                savedOrder.getCustomerId(),
                savedOrder.getTotalAmount(),
                eventItems
        );

        orderEventProducer.sendOrderCreatedEvent(orderCreatedEvent);
        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto.Response getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    private OrderDto.Response mapToResponse(Order order) {
        List<OrderDto.OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderDto.OrderItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getPrice(),
                        item.getQuantity()
                )).toList();

        return new OrderDto.Response(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                itemResponses
        );
    }
}