package com.burkina.marketplace.grpc.client;

import com.burkina.marketplace.dto.request.PayRequest;
import com.burkina.marketplace.dto.response.PaymentResponse;
import com.burkina.marketplace.exception.PaymentFailedException;
import com.burkina.marketplace.exception.PaymentServiceException;
import com.burkina.marketplace.mapper.PaymentMapper;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import marketplace.payment.Payment;
import marketplace.payment.PaymentServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentGrpcClient {

    private final PaymentMapper paymentMapper;

    @GrpcClient("payment-service")
    private PaymentServiceGrpc.PaymentServiceBlockingStub paymentServiceStub;

    public PaymentResponse pay(PayRequest payRequest) {
        Payment.PayRequest request = paymentMapper.toPayRequest(payRequest);

        try {
            Payment.PayResponse response = paymentServiceStub.pay(request);

            return paymentMapper.toPaymentResponse(response);
        } catch (StatusRuntimeException e) {
            throw switch (e.getStatus().getCode()) {
                case FAILED_PRECONDITION -> new PaymentFailedException(
                                String.format("Payment failed for saga %d", payRequest.sagaId())
                );

                default -> new PaymentServiceException(
                        String.format("Payment service returned %s", e.getStatus())
                );
            };
        }
    }

    public void refund(Long paymentId) {
        Payment.RefundRequest request = paymentMapper.toRefundRequest(paymentId);

        try {
            paymentServiceStub.refund(request);
        } catch (StatusRuntimeException e) {
            throw new PaymentServiceException(
                        String.format("Payment service returned %s", e.getMessage())
            );
        }
    }
}
