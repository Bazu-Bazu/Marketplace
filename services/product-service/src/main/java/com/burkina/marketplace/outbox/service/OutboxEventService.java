package com.burkina.marketplace.outbox.service;

import com.burkina.marketplace.outbox.entity.OutboxEvent;
import com.burkina.marketplace.outbox.enums.EventType;
import com.burkina.marketplace.outbox.repository.OutboxEventCustomRepository;
import com.burkina.marketplace.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventCustomRepository outboxEventCustomRepository;

    @Transactional
    public void saveEvent(EventType eventType, String payload) {
        String topic = eventType.getTopic();

        OutboxEvent event = OutboxEvent.builder()
                .topic(topic)
                .eventType(eventType)
                .payload(payload)
                .build();

        outboxEventRepository.save(event);
    }

    public List<OutboxEvent> fetchPendingEvents() {
        return outboxEventCustomRepository.reserveEvents(50);
    }

    @Transactional
    public void finishPublishing(List<Long> successIds, List<Long> failedIds) {
        if (!successIds.isEmpty()) {
            outboxEventRepository.markSent(successIds);
        }

        if (!failedIds.isEmpty()) {
            outboxEventRepository.markNew(failedIds);
        }
    }
}
