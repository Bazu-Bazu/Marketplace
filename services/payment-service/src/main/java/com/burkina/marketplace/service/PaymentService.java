package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.Payment;
import com.burkina.marketplace.domain.repository.PaymentRepository;
import com.burkina.marketplace.dto.data.PaymentCreateData;
import com.burkina.marketplace.exception.PaymentFailedException;
import com.burkina.marketplace.exception.PaymentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional(noRollbackFor = PaymentFailedException.class)
    public Payment createPayment(PaymentCreateData paymentCreateData) {
        Optional<Payment> existingPayment = paymentRepository.findBySagaId(paymentCreateData.sagaId());

        if (existingPayment.isPresent()) {
            return existingPayment.get();
        }

        Payment payment = Payment.builder()
                .userId(paymentCreateData.userId())
                .sagaId(paymentCreateData.sagaId())
                .amount(paymentCreateData.amount())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        payAttempt(savedPayment);

        return savedPayment;
    }

    private void payAttempt(Payment payment) {
        Random random = new Random();

        int result = random.nextInt(2);

        if (result == 0) {
            payment.pay();
        } else {
            payment.fail();

            throw new PaymentFailedException(
                            String.format("Payment failed for saga %d", payment.getSagaId()
                    ));
        }
    }

    @Transactional
    public void cancelPayment(Long paymentId) {
        Payment payment = getById(paymentId);

        payment.cancel();
    }

    @Transactional(readOnly = true)
    public Payment getById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        String.format("Payment with id %d not found", paymentId)
                ));
    }
}
