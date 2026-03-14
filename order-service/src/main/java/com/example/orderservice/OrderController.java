package com.example.orderservice;

import com.example.orderservice.entity.Order;
import com.example.orderservice.service.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private static final String TOPIC = "order-topic";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    public OrderController(KafkaTemplate<String, String> kafkaTemplate, OrderService orderService) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderService = orderService;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping
    public String createOrder(@RequestBody String orderJson) {
        try {
            JsonNode jsonNode = objectMapper.readTree(orderJson);
            String orderId = jsonNode.has("orderId") ? jsonNode.get("orderId").asText() : "ORD-" + System.currentTimeMillis();
            String item = jsonNode.has("item") ? jsonNode.get("item").asText() : "Unknown";
            Integer quantity = jsonNode.has("quantity") ? jsonNode.get("quantity").asInt() : 1;

            Order order = orderService.createOrder(orderId, item, quantity);

            kafkaTemplate.send(TOPIC, orderJson);
            logger.info("OrderCreated event published to Kafka topic: {}", TOPIC);
            return "Order Created (ID: " + order.getOrderId() + ") & Event Published";
        } catch (Exception e) {
            logger.error("Error processing order: {}", e.getMessage());
            return "Error creating order: " + e.getMessage();
        }
    }
}
