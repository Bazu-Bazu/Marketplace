package com.burkina.marketplace.outbox.repository;

import com.burkina.marketplace.outbox.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    @Modifying
    @Query("""
        UPDATE OutboxEvent o
        SET o.status = com.burkina.marketplace.outbox.enums.EventStatus.SENT
        WHERE o.id IN :ids
    """)
    void markSent(@Param("ids") List<Long> ids);

    @Modifying
    @Query("""
        UPDATE OutboxEvent o
        SET o.status = com.burkina.marketplace.outbox.enums.EventStatus.NEW
        WHERE o.id IN :ids
    """)
    void markNew(@Param("ids") List<Long> ids);
}