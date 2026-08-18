package com.marketplace.serviceProduct.service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.serviceProduct.dto.event.ProductEvent;
import com.marketplace.serviceProduct.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductEventHandler {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "products")
    public void changeProductRating(String message) throws JsonProcessingException {
        ProductEvent productEvent = objectMapper.readValue(message, ProductEvent.class);
        productService.changeProductRating(productEvent.getId(), productEvent.getRating());
    }

}
