package com.marketplace.serviceauth.service.event;

import com.marketplace.serviceauth.dto.event.UserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventPublisher {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public void sendUserIdToKafka(UserEvent event) {
        kafkaTemplate.send("users", event.getId().toString(), event);
    }

}
