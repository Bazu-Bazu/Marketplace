package com.burkina.marketplace.mapper;

import com.burkina.marketplace.domain.entity.OrderSaga;
import com.burkina.marketplace.dto.data.ValidatedCart;
import com.burkina.marketplace.dto.request.PayRequest;
import com.burkina.marketplace.dto.response.PaymentResponse;
import marketplace.payment.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentMapper {

    public Payment.PayRequest toPayRequest(PayRequest request) {
        return Payment.PayRequest.newBuilder()
                .setUserId(request.userId())
                .setSagaId(request.sagaId())
                .setAmount(request.amount().toString())
                .build();
    }

    public PaymentResponse toPaymentResponse(Payment.PayResponse response) {
        return PaymentResponse.builder()
                .paymentId(response.getPaymentId())
                .build();
    }

    public Payment.RefundRequest toRefundRequest(Long paymentId) {
        return Payment.RefundRequest.newBuilder()
                .setPaymentId(paymentId)
                .build();
    }

    public PayRequest toPayRequest(OrderSaga saga, ValidatedCart cart) {
        return PayRequest.builder()
                .userId(saga.getUserId())
                .sagaId(saga.getId())
                .amount(cart.getTotalPrice())
                .build();
    }
}
