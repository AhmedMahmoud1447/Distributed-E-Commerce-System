package com.ahmed.inventory_service.consumers;

import com.ahmed.inventory_service.com.ahmed.order_service.events.OrderCreatedEvent;
import com.ahmed.inventory_service.domain.Inventory;
import com.ahmed.inventory_service.events.InventoryFailedEvent;
import com.ahmed.inventory_service.events.InventoryReservedEvent;
import com.ahmed.inventory_service.producers.InventoryEventProducer;
import com.ahmed.inventory_service.repositories.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCreatedConsumerTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryEventProducer inventoryEventProducer;

    @InjectMocks
    private OrderCreatedConsumer orderCreatedConsumer;

    @Test
    void consumeOrderCreatedEvent_Success() {
        OrderCreatedEvent.OrderLineItemEvent itemEvent = new OrderCreatedEvent.OrderLineItemEvent(200L, 3);
        OrderCreatedEvent event = new OrderCreatedEvent(1L, "ORD-123", 10L, BigDecimal.TEN, List.of(itemEvent));

        Inventory inventory = new Inventory(1L, 200L, 10);

        when(inventoryRepository.findByProductId(200L)).thenReturn(Optional.of(inventory));

        orderCreatedConsumer.consumeOrderCreatedEvent(event);

        assertEquals(7, inventory.getAvailableQuantity());
        verify(inventoryRepository, times(1)).saveAll(anyList());

        ArgumentCaptor<InventoryReservedEvent> captor = ArgumentCaptor.forClass(InventoryReservedEvent.class);
        verify(inventoryEventProducer, times(1)).sendReservedEvent(captor.capture());
        assertEquals("ORD-123", captor.getValue().orderNumber());
    }

    @Test
    void consumeOrderCreatedEvent_Failed_InsufficientStock() {
        OrderCreatedEvent.OrderLineItemEvent itemEvent = new OrderCreatedEvent.OrderLineItemEvent(200L, 15);
        OrderCreatedEvent event = new OrderCreatedEvent(1L, "ORD-123", 10L, BigDecimal.TEN, List.of(itemEvent));

        Inventory inventory = new Inventory(1L, 200L, 10);

        when(inventoryRepository.findByProductId(200L)).thenReturn(Optional.of(inventory));

        orderCreatedConsumer.consumeOrderCreatedEvent(event);

        assertEquals(10, inventory.getAvailableQuantity());
        verify(inventoryRepository, never()).saveAll(anyList());

        ArgumentCaptor<InventoryFailedEvent> captor = ArgumentCaptor.forClass(InventoryFailedEvent.class);
        verify(inventoryEventProducer, times(1)).sendFailedEvent(captor.capture());
        assertEquals("In-sufficient stock for Product ID: 200", captor.getValue().reason());
    }
}