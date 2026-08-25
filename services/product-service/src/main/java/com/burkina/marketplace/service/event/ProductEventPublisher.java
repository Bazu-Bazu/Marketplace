package com.burkina.marketplace.service.event;

import com.burkina.marketplace.domain.entity.Product;
import com.burkina.marketplace.exception.EventSerializationException;
import com.burkina.marketplace.mapper.ProductMapper;
import com.burkina.marketplace.outbox.enums.EventType;
import com.burkina.marketplace.outbox.service.OutboxEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductEventPublisher {

    private final ObjectMapper objectMapper;
    private final ProductMapper productMapper;
    private final OutboxEventService outboxEventService;

    public void publishProductPublished(Product product) {
        publish(
                EventType.PRODUCT_PUBLISHED,
                productMapper.toProductPublishedEvent(product)
        );
    }

    public void publishProductLocked(Product product) {
        publish(
                EventType.PRODUCT_LOCKED,
                productMapper.toProductLockedEvent(product)
        );
    }

    public void publishProductUnlocked(Product product) {
        publish(
                EventType.PRODUCT_UNLOCKED,
                productMapper.toProductUnlockedEvent(product)
        );
    }

    public void publishProductUpdated(Product product) {
        publish(
                EventType.PRODUCT_UPDATED,
                productMapper.toProductUpdatedEvent(product)
        );
    }

    public void publishProductRecalled(Product product) {
        publish(
                EventType.PRODUCT_RECALLED,
                productMapper.toProductRecalledEvent(product)
        );
    }

    private void publish(EventType type, Object event) {
        try {
            outboxEventService.saveEvent(
                    type,
                    objectMapper.writeValueAsString(event)
            );
        } catch (JsonProcessingException e) {
            throw new EventSerializationException(
                    String.format("Failed to serialize event. Exception: %s", e)
            );
        }
    }
}
