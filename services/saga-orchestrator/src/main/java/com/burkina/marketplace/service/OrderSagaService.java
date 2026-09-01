package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.OrderSaga;
import com.burkina.marketplace.domain.enums.SagaStep;
import com.burkina.marketplace.domain.repository.OrderSagaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderSagaService {

    private final OrderSagaRepository orderSagaRepository;

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
}