package com.example.billingservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BillingConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BillingConsumer.class);

    @KafkaListener(topics = "order-topic", groupId = "billing-group")
    public void consumeOrderEvent(String order) {
        logger.info("Billing Service received OrderCreated event: {}", order);
        logger.info("Generating invoice for order: {}", order);
        simulateInvoiceGeneration(order);
        logger.info("Invoice generated successfully for order: {}", order);
    }

    private void simulateInvoiceGeneration(String order) {
        // Simulate invoice generation logic
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.info("Invoice generation completed");
    }
}
