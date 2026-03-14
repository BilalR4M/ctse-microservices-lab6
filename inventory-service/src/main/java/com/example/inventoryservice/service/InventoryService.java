package com.example.inventoryservice.service;

import com.example.inventoryservice.entity.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Inventory updateStock(String itemName, int quantityOrdered) {
        Inventory inventory = inventoryRepository.findByItemName(itemName)
                .orElseGet(() -> {
                    Inventory newInventory = new Inventory(itemName, 100);
                    return inventoryRepository.save(newInventory);
                });

        inventory.deductStock(quantityOrdered);
        inventory.setLastUpdated(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        Inventory saved = inventoryRepository.save(inventory);
        logger.info("Inventory updated: {} - Remaining stock: {}", itemName, saved.getQuantity());
        return saved;
    }
}
