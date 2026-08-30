package com.burkina.marketplace.kafka.consumer;

import com.burkina.common.dto.event.marketplace.product.ProductLockedEvent;
import com.burkina.common.dto.event.marketplace.product.ProductPublishedEvent;
import com.burkina.common.dto.event.marketplace.product.ProductRecalledEvent;
import com.burkina.common.dto.event.marketplace.product.ProductUnlockedEvent;
import com.burkina.marketplace.service.InventoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductConsumer {

    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;

    @KafkaListener(topics = "product-published")
    public void handleProductPublished(String message, Acknowledgment ack) throws JsonProcessingException {
        ProductPublishedEvent event = objectMapper.readValue(message, ProductPublishedEvent.class);
        inventoryService.createInventory(event);

        ack.acknowledge();
    }

    @KafkaListener(topics = "product-locked")
    public void handleProductLocked(String message, Acknowledgment ack) throws JsonProcessingException {
        ProductLockedEvent event = objectMapper.readValue(message, ProductLockedEvent.class);
        inventoryService.inactivateInventory(event);

        ack.acknowledge();
    }

    @KafkaListener(topics = "product-unlocked")
    public void handleProductUnlocked(String message, Acknowledgment ack) throws JsonProcessingException {
        ProductUnlockedEvent event = objectMapper.readValue(message, ProductUnlockedEvent.class);
        inventoryService.activateInventory(event);

        ack.acknowledge();
    }

    @KafkaListener(topics = "product-recalled")
    public void handleProductRecalled(String message, Acknowledgment ack) throws JsonProcessingException {
        ProductRecalledEvent event = objectMapper.readValue(message, ProductRecalledEvent.class);
        inventoryService.inactivateInventory(event);

        ack.acknowledge();
    }
}
