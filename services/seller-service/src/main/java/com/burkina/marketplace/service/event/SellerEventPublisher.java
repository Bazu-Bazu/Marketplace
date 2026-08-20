package com.burkina.marketplace.service.event;

import com.burkina.marketplace.domain.entity.Seller;
import com.burkina.marketplace.exception.EventSerializationException;
import com.burkina.marketplace.mapper.SellerMapper;
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
public class SellerEventPublisher {

    private final SellerMapper sellerMapper;
    private final ObjectMapper objectMapper;
    private final OutboxEventService outboxEventService;

    public void publishSellerRegistration(Seller seller) {
        publish(
                EventType.SELLER_REGISTERED,
                sellerMapper.toSellerRegisteredEvent(seller)
        );
    }

    public void publishSellerLocked(Seller seller) {
        publish(
                EventType.SELLER_LOCKED,
                sellerMapper.toSellerLockedEvent(seller)
        );
    }

    public void publishSellerUnlocked(Seller seller) {
        publish(
                EventType.SELLER_UNLOCKED,
                sellerMapper.toSellerUnlockedEvent(seller)
        );
    }

    public void publishSellerDeleted(Seller seller) {
        publish(
                EventType.SELLER_DELETED,
                sellerMapper.toSellerDeletedEvent(seller)
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
