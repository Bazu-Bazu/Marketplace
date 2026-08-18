package com.marketplace.serviceOrder.service.grpc;

import com.marketplace.serviceOrder.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentGrpcClient {

    public String createPayment(Order order) {
        return "paymentID";
    }

}
