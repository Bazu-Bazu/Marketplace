package com.marketplace.serviceauth.service.event;

import com.marketplace.serviceauth.dto.event.UserEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private UserEventPublisher userEventPublisher;

    private UserEvent event;

    @BeforeEach
    void setUp() {
        event = UserEvent.createTestEvent(1L, "test@email.com", "John", "Smith");
    }

    @Test
    void shouldSendUserEventToCorrectTopic() {
        String expectedTopic = "users";
        String expectedKey = event.getId().toString();

        userEventPublisher.sendUserToKafka(event);

        verify(kafkaTemplate).send(expectedTopic, expectedKey, event);
    }

}
