package com.burkina.marketplace.mapper;

import com.burkina.marketplace.dto.data.PaymentCreateData;
import com.burkina.marketplace.exception.IllegalPaymentStatusException;
import marketplace.payment.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentMapper {

    public PaymentCreateData toPaymentCreateData(Payment.PayRequest request) {
        return PaymentCreateData.builder()
                .userId(request.getUserId())
                .sagaId(request.getSagaId())
                .amount(new BigDecimal(request.getAmount()))
                .build();
    }

    public Payment.PayResponse toPayResponse(com.burkina.marketplace.domain.entity.Payment payment) {
        return Payment.PayResponse.newBuilder()
                    .setPaymentId(payment.getId())
                    .setStatus(getStatus(payment))
                    .build();
    }

    private Payment.PaymentStatus getStatus(com.burkina.marketplace.domain.entity.Payment payment) {
        return switch (payment.getStatus()) {
            case PAID -> Payment.PaymentStatus.SUCCESS;
            case CANCELLED -> Payment.PaymentStatus.FAILED;
            default -> throw new IllegalPaymentStatusException(
                    String.format("Payment status %s is not supported", payment.getStatus())
            );
        };
    }
}
