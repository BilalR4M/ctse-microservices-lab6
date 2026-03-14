package com.example.billingservice.service;

import com.example.billingservice.entity.Invoice;
import com.example.billingservice.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class BillingService {

    private static final Logger logger = LoggerFactory.getLogger(BillingService.class);
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal("999.99");

    private final InvoiceRepository invoiceRepository;

    public BillingService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public Invoice generateInvoice(String orderId, String item, Integer quantity) {
        String invoiceId = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        BigDecimal totalAmount = DEFAULT_PRICE.multiply(new BigDecimal(quantity));
        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Invoice invoice = new Invoice(invoiceId, orderId, item, quantity, totalAmount);
        invoice.setCreatedAt(createdAt);
        Invoice saved = invoiceRepository.save(invoice);
        logger.info("Invoice generated: {} for order: {}, Total: ${}", saved.getInvoiceId(), orderId, totalAmount);
        return saved;
    }
}
