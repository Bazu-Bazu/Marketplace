package com.burkina.marketplace.kafka.consumer;

import com.burkina.common.dto.event.marketplace.product.*;
import com.burkina.marketplace.service.SearchProductService;
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
    private final SearchProductService productService;

    @KafkaListener(topics = "product-published")
    public void handleProductPublished(String message, Acknowledgment ack) throws JsonProcessingException {
        ProductPublishedEvent event = objectMapper.readValue(message, ProductPublishedEvent.class);
        productService.createProduct(event);

        ack.acknowledge();
    }

    @KafkaListener(topics = "product-locked")
    public void handleProductLocked(String message, Acknowledgment ack) throws JsonProcessingException {
        ProductLockedEvent event = objectMapper.readValue(message, ProductLockedEvent.class);
        productService.lockProduct(event);

        ack.acknowledge();
    }

    @KafkaListener(topics = "product-unlocked")
    public void handleProductUnlocked(String message, Acknowledgment ack) throws JsonProcessingException {
        ProductUnlockedEvent event = objectMapper.readValue(message, ProductUnlockedEvent.class);
        productService.unlockProduct(event);

        ack.acknowledge();
    }

    @KafkaListener(topics = "product-updated")
    public void handleProductUpdated(String message, Acknowledgment ack) throws JsonProcessingException {
        ProductUpdatedEvent event = objectMapper.readValue(message, ProductUpdatedEvent.class);
        productService.updateProduct(event);

        ack.acknowledge();
    }

    @KafkaListener(topics = "product-recalled")
    public void handleProductRecalled(String message, Acknowledgment ack) throws JsonProcessingException {
        ProductRecalledEvent event = objectMapper.readValue(message, ProductRecalledEvent.class);
        productService.recallProduct(event);

        ack.acknowledge();
    }
}
