package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.OrderSaga;
import com.burkina.marketplace.grpc.client.InventoryGrpcClient;
import com.burkina.marketplace.grpc.client.OrderGrpcClient;
import com.burkina.marketplace.grpc.client.PaymentGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderSagaCompensationService {

    private final OrderGrpcClient orderGrpcClient;
    private final PaymentGrpcClient paymentGrpcClient;
    private final InventoryGrpcClient inventoryGrpcClient;

    public void compensate(OrderSaga saga) {
        if (saga.getOrderId() != null) {
            cancelOrder(saga);
        }

        if (saga.getPaymentId() != null) {
            refundPayment(saga);
        }

        if (saga.getReservationId() != null) {
            releaseInventory(saga);
        }
    }

    private void cancelOrder(OrderSaga saga) {
        orderGrpcClient.cancelOrder(saga.getOrderId());
    }

    private void refundPayment(OrderSaga saga) {
        paymentGrpcClient.refund(saga.getPaymentId());
    }

    private void releaseInventory(OrderSaga saga) {
        inventoryGrpcClient.release(saga.getReservationId());
    }
}
