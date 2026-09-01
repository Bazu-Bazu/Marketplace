package com.burkina.marketplace.grpc.client;

import com.burkina.marketplace.dto.response.PaymentResponse;
import com.burkina.marketplace.exception.PaymentServiceUnavailableException;
import com.burkina.marketplace.mapper.PaymentMapper;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import marketplace.payment.Payment;
import marketplace.payment.PaymentServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PaymentGrpcClient {

    private final PaymentMapper paymentMapper;

    @GrpcClient("payment-service")
    private PaymentServiceGrpc.PaymentServiceBlockingStub paymentServiceStub;

    public PaymentResponse pay(Long userId, Long sagaId, BigDecimal amount) {
        Payment.PayRequest request = paymentMapper.toPayRequest(userId, sagaId, amount);

        try {
            Payment.PayResponse response = paymentServiceStub.pay(request);

            return paymentMapper.toPaymentResponse(response);
        } catch (StatusRuntimeException e) {
            throw new PaymentServiceUnavailableException(
                    String.format("Payment service is unavailable: %s", e.getMessage())
            );
        }
    }

    public void refund(Long paymentId) {
        Payment.RefundRequest request = paymentMapper.toRefundRequest(paymentId);

        try {
            paymentServiceStub.refund(request);
        } catch (StatusRuntimeException e) {
            throw new PaymentServiceUnavailableException(
                    String.format("Payment service is unavailable: %s", e.getMessage())
            );
        }
    }
}
