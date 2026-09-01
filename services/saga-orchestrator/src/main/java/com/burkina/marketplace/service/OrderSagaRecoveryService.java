package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.repository.OrderSagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderSagaRecoveryService {

    private final OrderSagaService sagaService;
    private final OrderSagaRepository sagaRepository;
    private final OrderSagaCompensationService compensationService;

    @Scheduled(fixedDelay = 5000)
    public void recover() {

        List<OrderSaga> sagas =
                sagaRepository.findByStatusIn(
                        List.of(
                                SagaStatus.STARTED,
                                SagaStatus.PROCESSING,
                                SagaStatus.COMPENSATING
                        )
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
            log.error(
                    "Failed to recover saga {}",
                    saga.getId(),
                    e
            );
        }
    }
}
