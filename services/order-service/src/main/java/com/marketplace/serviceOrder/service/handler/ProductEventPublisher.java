package com.marketplace.serviceOrder.service.handler;

import com.marketplace.serviceOrder.dto.event.ProductEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendProductToKafka(ProductEvent event) {
        kafkaTemplate.send("products", event.getId().toString(), event);
    }

}
