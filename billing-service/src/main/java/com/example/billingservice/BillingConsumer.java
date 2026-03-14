package com.example.billingservice;

import com.example.billingservice.service.BillingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BillingConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BillingConsumer.class);

    private final BillingService billingService;
    private final ObjectMapper objectMapper;

    public BillingConsumer(BillingService billingService) {
        this.billingService = billingService;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = "order-topic", groupId = "billing-group")
    public void consumeOrderEvent(String order) {
        logger.info("Billing Service received OrderCreated event: {}", order);
        try {
            JsonNode jsonNode = objectMapper.readTree(order);
            String orderId = jsonNode.has("orderId") ? jsonNode.get("orderId").asText() : "ORD-UNKNOWN";
            String item = jsonNode.has("item") ? jsonNode.get("item").asText() : "Unknown";
            int quantity = jsonNode.has("quantity") ? jsonNode.get("quantity").asInt() : 1;

            logger.info("Generating invoice for order: {}, item: {}, quantity: {}", orderId, item, quantity);
            billingService.generateInvoice(orderId, item, quantity);
            logger.info("Invoice generated successfully for order: {}", orderId);
        } catch (Exception e) {
            logger.error("Error processing order event: {}", e.getMessage());
        }
    }
}
