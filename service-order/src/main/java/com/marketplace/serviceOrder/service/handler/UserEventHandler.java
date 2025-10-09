package com.marketplace.serviceOrder.service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.serviceOrder.dto.event.UserEvent;
import com.marketplace.serviceOrder.service.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventHandler {

    private final BasketService basketService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "users")
    public void createBasketForUser(String message) throws JsonProcessingException {
        UserEvent userEvent = objectMapper.readValue(message, UserEvent.class);
        basketService.createBasket(userEvent.getId());
        System.out.println("order");
    }

}
