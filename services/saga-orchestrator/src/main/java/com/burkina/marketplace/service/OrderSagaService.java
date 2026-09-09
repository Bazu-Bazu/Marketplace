package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.OrderSaga;
import com.burkina.marketplace.domain.enums.SagaStep;
import com.burkina.marketplace.domain.repository.OrderSagaRepository;
import com.burkina.marketplace.service.event.OrderEventPublisher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderSagaService {

    private final OrderSagaRepository orderSagaRepository;
    private final OrderEventPublisher orderEventPublisher;

    @Transactional
    public OrderSaga create(Long userId) {
        OrderSaga saga = OrderSaga.builder()
                .userId(userId)
                .build();

        return save(saga);
    }

    @Transactional
    public void prepareStep(OrderSaga saga, SagaStep step) {
        saga.moveTo(step);
        save(saga);
    }

    @Transactional
    public OrderSaga save(OrderSaga saga) {
        return orderSagaRepository.save(saga);
    }

    @Transactional
    public void completeSaga(OrderSaga saga) {
        saga.complete();
        orderSagaRepository.save(saga);

        orderEventPublisher.publishOrderCreated(saga);
    }
}