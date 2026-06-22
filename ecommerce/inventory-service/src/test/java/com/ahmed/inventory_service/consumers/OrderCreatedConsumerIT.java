package com.ahmed.inventory_service.consumers;

import com.ahmed.inventory_service.com.ahmed.order_service.events.OrderCreatedEvent;
import com.ahmed.inventory_service.domain.Inventory;
import com.ahmed.inventory_service.repositories.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class OrderCreatedConsumerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {
        inventoryRepository.deleteAll();
    }

    @Test
    void shouldConsumeEventAndDeductStock() {
        Inventory inventory = Inventory.builder()
                .productId(500L)
                .availableQuantity(20)
                .build();
        inventoryRepository.save(inventory);

        OrderCreatedEvent.OrderLineItemEvent item = new OrderCreatedEvent.OrderLineItemEvent(500L, 5);
        OrderCreatedEvent event = new OrderCreatedEvent(10L, "ORD-999", 1L, BigDecimal.valueOf(100), List.of(item));

        kafkaTemplate.send("order-created-events", "ORD-999", event);

        await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    Optional<Inventory> updatedInventory = inventoryRepository.findByProductId(500L);
                    assertTrue(updatedInventory.isPresent());
                    assertEquals(15, updatedInventory.get().getAvailableQuantity());
                });
    }
}