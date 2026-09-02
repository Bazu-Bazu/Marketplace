package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.OrderSaga;
import com.burkina.marketplace.domain.enums.SagaStatus;
import com.burkina.marketplace.domain.repository.OrderSagaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderSagaRecoveryService {

    private final OrderSagaService sagaService;
    private final OrderSagaRepository sagaRepository;
    private final OrderSagaCompensationService compensationService;

    @Scheduled(fixedDelay = 100000)
    public void recover() {
        Instant threshold = Instant.now().minus(5, ChronoUnit.MINUTES);

        List<OrderSaga> sagas =
                sagaRepository.findByStatusInAndUpdatedAtBefore(
                        List.of(SagaStatus.STARTED, SagaStatus.PROCESSING, SagaStatus.COMPENSATING),
                        threshold
                );

        for (OrderSaga saga : sagas) {
            recover(saga);
        }
    }

    private void recover(OrderSaga saga) {
        try {
            saga.startCompensation();
            sagaService.save(saga);

            compensationService.compensate(saga);

            saga.fail();
            sagaService.save(saga);
        } catch (Exception e) {
            log.error("Failed to recover saga {}", saga.getId(), e);
        }
    }
}
