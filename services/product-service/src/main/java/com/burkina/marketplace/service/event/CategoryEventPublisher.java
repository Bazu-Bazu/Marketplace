package com.burkina.marketplace.service.event;

import com.burkina.marketplace.domain.entity.Category;
import com.burkina.marketplace.exception.EventSerializationException;
import com.burkina.marketplace.mapper.CategoryMapper;
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
public class CategoryEventPublisher {

    private final ObjectMapper objectMapper;
    private final CategoryMapper categoryMapper;
    private final OutboxEventService outboxEventService;

    public void publishCategoryCreated(Category category) {
        publish(
                EventType.CATEGORY_CREATED,
                categoryMapper.toCategoryCreatedEvent(category)
        );
    }

    public void publishCategoryInactivated(Category category) {
        publish(
                EventType.CATEGORY_INACTIVATED,
                categoryMapper.toCategoryInactivatedEvent(category)
        );
    }

    public void publishCategoryActivated(Category category) {
        publish(
                EventType.CATEGORY_ACTIVATED,
                categoryMapper.toCategoryActivatedEvent(category)
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
