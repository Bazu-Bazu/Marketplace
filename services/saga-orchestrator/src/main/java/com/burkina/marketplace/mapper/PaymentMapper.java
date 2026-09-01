package com.burkina.marketplace.mapper;

import com.burkina.marketplace.dto.response.PaymentResponse;
import marketplace.payment.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentMapper {

    public Payment.PayRequest toPayRequest(Long userId, Long sagaId, BigDecimal amount) {
        return Payment.PayRequest.newBuilder()
                .setUserId(userId)
                .setSagaId(sagaId)
                .setAmount(amount.toString())
                .build();
    }

    public PaymentResponse toPaymentResponse(Payment.PayResponse response) {
        PaymentResponse.PaymentStatus status = switch (response.getStatus()) {
            case SUCCESS -> PaymentResponse.PaymentStatus.SUCCESS;
            case FAILED -> PaymentResponse.PaymentStatus.FAILED;
            default -> throw new IllegalArgumentException("Unknown status: " + response.getStatus());
        };

        return PaymentResponse.builder()
                .paymentId(response.getPaymentId())
                .status(status)
                .build();
    }

    public Payment.RefundRequest toRefundRequest(Long paymentId) {
        return Payment.RefundRequest.newBuilder()
                .setPaymentId(paymentId)
                .build();
    }
}
