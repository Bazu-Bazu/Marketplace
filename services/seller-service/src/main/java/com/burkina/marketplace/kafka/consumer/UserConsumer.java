package com.burkina.marketplace.kafka.consumer;

import com.burkina.common.dto.event.user.UserDeletedEvent;
import com.burkina.common.dto.event.user.UserLockedEvent;
import com.burkina.marketplace.service.SellerCommandService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConsumer {

    private final ObjectMapper objectMapper;
    private final SellerCommandService sellerCommandService;

    @KafkaListener(topics = "user-deleted")
    public void handleUserDeletedEvent(String message, Acknowledgment ack) throws JsonProcessingException {
        UserDeletedEvent event = objectMapper.readValue(message, UserDeletedEvent.class);
        sellerCommandService.delete(event.userId());

        ack.acknowledge();
    }

    @KafkaListener(topics = "user-locked")
    public void handleUserLockedEvent(String message, Acknowledgment ack) throws JsonProcessingException {
        UserLockedEvent event = objectMapper.readValue(message, UserLockedEvent.class);
        sellerCommandService.lockByUserId(event.userId());

        ack.acknowledge();
    }

    @KafkaListener(topics = "user-unlocked")
    public void handleUserUnlockedEvent(String message, Acknowledgment ack) throws JsonProcessingException {
        UserLockedEvent event = objectMapper.readValue(message, UserLockedEvent.class);
        sellerCommandService.unlockByUserId(event.userId());

        ack.acknowledge();
    }
}
