package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.OrderSaga;
import com.burkina.marketplace.domain.enums.SagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Repository
public interface OrderSagaRepository extends JpaRepository<OrderSaga, Long> {

    List<OrderSaga> findByStatusInAndUpdatedAtBefore(Collection<SagaStatus> statuses, Instant threshold);
}
