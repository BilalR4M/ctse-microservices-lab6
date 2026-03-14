package com.example.inventoryservice;

import com.example.inventoryservice.service.InventoryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryConsumer {

    private static final Logger logger = LoggerFactory.getLogger(InventoryConsumer.class);

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    public InventoryConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = "order-topic", groupId = "inventory-group")
    public void consumeOrderEvent(String order) {
        logger.info("Inventory Service received OrderCreated event: {}", order);
        try {
            JsonNode jsonNode = objectMapper.readTree(order);
            String item = jsonNode.has("item") ? jsonNode.get("item").asText() : "Unknown";
            int quantity = jsonNode.has("quantity") ? jsonNode.get("quantity").asInt() : 1;

            logger.info("Updating stock for item: {}, quantity: {}", item, quantity);
            inventoryService.updateStock(item, quantity);
            logger.info("Stock updated successfully for item: {}", item);
        } catch (Exception e) {
            logger.error("Error processing order event: {}", e.getMessage());
        }
    }
}
