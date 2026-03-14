package com.example.orderservice.service;

import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(String orderId, String item, Integer quantity) {
        Order order = new Order(orderId, item, quantity);
        Order savedOrder = orderRepository.save(order);
        logger.info("Order saved to database: {}", savedOrder);
        return savedOrder;
    }
}
