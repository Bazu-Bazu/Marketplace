package com.burkina.marketplace.kafka.consumer;

import com.burkina.common.dto.event.marketplace.category.CategoryActivatedEvent;
import com.burkina.common.dto.event.marketplace.category.CategoryCreatedEvent;
import com.burkina.common.dto.event.marketplace.category.CategoryInactivatedEvent;
import com.burkina.marketplace.service.SearchCategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryConsumer {

    private final ObjectMapper objectMapper;
    private final SearchCategoryService categoryService;

    @KafkaListener(topics = "category-created")
    public void handleCategoryCreated(String message, Acknowledgment ack) throws JsonProcessingException {
        CategoryCreatedEvent event = objectMapper.readValue(message, CategoryCreatedEvent.class);
        categoryService.createCategory(event);

        ack.acknowledge();
    }

    @KafkaListener(topics = "category-inactivated")
    public void handleCategoryInactivated(String message, Acknowledgment ack) throws JsonProcessingException {
        CategoryInactivatedEvent event = objectMapper.readValue(message, CategoryInactivatedEvent.class);
        categoryService.inactivateCategory(event);

        ack.acknowledge();
    }

    @KafkaListener(topics = "category-activated")
    public void handleCategoryActivated(String message, Acknowledgment ack) throws JsonProcessingException {
        CategoryActivatedEvent event = objectMapper.readValue(message, CategoryActivatedEvent.class);
        categoryService.activateCategory(event);

        ack.acknowledge();
    }
}
