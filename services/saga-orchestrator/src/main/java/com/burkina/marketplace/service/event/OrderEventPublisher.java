package com.burkina.marketplace.service.event;

import com.burkina.marketplace.domain.entity.OrderSaga;
import com.burkina.marketplace.exception.EventSerializationException;
import com.burkina.marketplace.mapper.OrderMapper;
import com.burkina.marketplace.outbox.enums.EventType;
import com.burkina.marketplace.outbox.service.OutboxEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final OrderMapper orderMapper;
    private final ObjectMapper objectMapper;
    private final OutboxEventService outboxEventService;

    public void publishOrderCreated(OrderSaga saga) {
        publish(
                EventType.ORDER_CREATED,
                orderMapper.toOrderCreatedEvent(saga)
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
