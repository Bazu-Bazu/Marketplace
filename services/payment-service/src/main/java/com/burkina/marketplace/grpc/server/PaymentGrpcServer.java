package com.burkina.marketplace.grpc.server;

import com.burkina.marketplace.mapper.PaymentMapper;
import com.burkina.marketplace.service.PaymentService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import marketplace.payment.Payment;
import marketplace.payment.PaymentServiceGrpc;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class PaymentGrpcServer extends PaymentServiceGrpc.PaymentServiceImplBase {

    private final PaymentMapper paymentMapper;
    private final PaymentService paymentService;

    @Override
    public void pay(Payment.PayRequest request, StreamObserver<Payment.PayResponse> responseObserver) {
        try {
            var payment = paymentService.createPayment(paymentMapper.toPaymentCreateData(request));

            responseObserver.onNext(paymentMapper.toPayResponse(payment));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void refund(Payment.RefundRequest request, StreamObserver<Payment.RefundResponse> responseObserver) {
        try {
            paymentService.cancelPayment(request.getPaymentId());

            responseObserver.onNext(Payment.RefundResponse.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
