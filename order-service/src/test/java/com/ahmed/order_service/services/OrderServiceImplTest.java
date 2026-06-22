package com.ahmed.order_service.services;

import com.ahmed.order_service.client.ProductClient;
import com.ahmed.order_service.domain.Order;
import com.ahmed.order_service.domain.OrderStatus;
import com.ahmed.order_service.dtos.OrderDto;
import com.ahmed.order_service.dtos.ProductResponse;
import com.ahmed.order_service.events.OrderCreatedEvent;
import com.ahmed.order_service.producers.OrderEventProducer;
import com.ahmed.order_service.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderEventProducer orderEventProducer;
    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderDto.Request orderRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        OrderDto.OrderItemRequest itemRequest = new OrderDto.OrderItemRequest(101L, 2);
        orderRequest = new OrderDto.Request(1L, List.of(itemRequest));

        productResponse = new ProductResponse(101L, "Test Product", "SKU101", new BigDecimal("150.00"));
    }

    @Test
    void createOrder_ShouldSuccess_WhenProductExists() {
        // Arrange
        when(productClient.getProductById(101L)).thenReturn(productResponse);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order orderToSave = invocation.getArgument(0);
            orderToSave.setId(1L);
            return orderToSave;
        });

        // Act
        OrderDto.Response response = orderService.createOrder(orderRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(1L, response.customerId());
        assertEquals(OrderStatus.PENDING.name(), response.status());

        assertEquals(new BigDecimal("300.00"), response.totalAmount());
        assertEquals(1, response.items().size());
        assertEquals(new BigDecimal("150.00"), response.items().get(0).price());

        verify(orderRepository, times(1)).save(any(Order.class));

        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(orderEventProducer, times(1)).sendOrderCreatedEvent(eventCaptor.capture());

        OrderCreatedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(1L, capturedEvent.orderId());
        assertEquals(1L, capturedEvent.customerId());
        assertEquals(new BigDecimal("300.00"), capturedEvent.totalAmount());
        assertEquals(1, capturedEvent.items().size());
        assertEquals(101L, capturedEvent.items().get(0).productId());
    }
}