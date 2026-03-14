package com.example.inventoryservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryConsumer {

    private static final Logger logger = LoggerFactory.getLogger(InventoryConsumer.class);

    @KafkaListener(topics = "order-topic", groupId = "inventory-group")
    public void consumeOrderEvent(String order) {
        logger.info("Inventory Service received OrderCreated event: {}", order);
        logger.info("Updating stock for order: {}", order);
        simulateStockUpdate(order);
        logger.info("Stock updated successfully for order: {}", order);
    }

    private void simulateStockUpdate(String order) {
        // Simulate stock update logic
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.info("Stock deduction completed");
    }
}
