package com.burkina.marketplace.kafka.consumer;

import com.burkina.common.dto.event.marketplace.order.OrderCreatedEvent;
import com.burkina.marketplace.service.ReservationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderConsumer {

    private final ObjectMapper objectMapper;
    private final ReservationService reservationService;

    @KafkaListener(topics = "order-created")
    public void handleOrderCreated(String message, Acknowledgment ack) throws JsonProcessingException {
        OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
        reservationService.confirm(event);

        ack.acknowledge();
    }
}
