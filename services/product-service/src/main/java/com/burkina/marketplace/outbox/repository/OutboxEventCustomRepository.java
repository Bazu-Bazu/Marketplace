package com.burkina.marketplace.outbox.repository;

import com.burkina.marketplace.outbox.entity.OutboxEvent;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OutboxEventCustomRepository {

    private final EntityManager entityManager;

    @Transactional
    public List<OutboxEvent> reserveEvents(int limit) {
        return entityManager.createNativeQuery("""
            UPDATE outbox_events
            SET status = 'PROCESSING'
            WHERE id IN (
                SELECT id
                FROM outbox_events
                WHERE status = 'NEW'
                ORDER BY created_at
                LIMIT :limit
                FOR UPDATE SKIP LOCKED
            )
            RETURNING *
            """, OutboxEvent.class)
                .setParameter("limit", limit)
                .getResultList();
    }
}
