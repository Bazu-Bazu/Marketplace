package com.burkina.marketplace.kafka.consumer;

import com.burkina.common.dto.event.SellerDeletedEvent;
import com.burkina.common.dto.event.SellerLockedEvent;
import com.burkina.common.dto.event.SellerRegisteredEvent;
import com.burkina.common.dto.event.SellerUnlockedEvent;
import com.burkina.marketplace.service.SellerAccessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SellerConsumer {

    private final ObjectMapper objectMapper;
    private final SellerAccessService sellerAccessService;

    @KafkaListener(topics = "seller-registered")
    public void handleSellerRegistered(String message, Acknowledgment ack) throws JsonProcessingException {
        SellerRegisteredEvent event = objectMapper.readValue(message, SellerRegisteredEvent.class);
        sellerAccessService.createSellerAccess(event);

        ack.acknowledge();
    }

    @KafkaListener(topics = "seller-locked")
    public void handleSellerLocked(String message, Acknowledgment ack) throws JsonProcessingException {
        SellerLockedEvent event = objectMapper.readValue(message, SellerLockedEvent.class);
        sellerAccessService.lockSeller(event);

        ack.acknowledge();
    }

    @KafkaListener(topics = "seller-unlocked")
    public void handleSellerUnlocked(String message, Acknowledgment ack) throws JsonProcessingException {
        SellerUnlockedEvent event = objectMapper.readValue(message, SellerUnlockedEvent.class);
        sellerAccessService.unlockSeller(event);

        ack.acknowledge();
    }

    @KafkaListener(topics = "seller-deleted")
    public void handleSellerDeleted(String message, Acknowledgment ack) throws JsonProcessingException {
        SellerDeletedEvent event = objectMapper.readValue(message, SellerDeletedEvent.class);
        sellerAccessService.deleteSeller(event);

        ack.acknowledge();
    }
}
