package com.burkina.marketplace.grpc.client;

import com.burkina.marketplace.dto.request.CreateOrderRequest;
import com.burkina.marketplace.dto.response.OrderResponse;
import com.burkina.marketplace.exception.OrderServiceUnavailableException;
import com.burkina.marketplace.mapper.OrderMapper;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import marketplace.order.Order;
import marketplace.order.OrderServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderGrpcClient {

    private final OrderMapper orderMapper;

    @GrpcClient("order-service")
    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub;

    public OrderResponse createOrder(CreateOrderRequest orderRequest) {
        Order.CreateOrderRequest request = orderMapper.toCreateOrderRequest(orderRequest);

        try {
            Order.CreateOrderResponse response = orderServiceStub.createOrder(request);

            return orderMapper.toOrderResponse(response);
        } catch (StatusRuntimeException e) {
            throw new OrderServiceUnavailableException(
                    String.format("Order service is unavailable: %s", e.getMessage())
            );
        }
    }

    public void cancelOrder(Long orderId) {
        Order.CancelOrderRequest request = orderMapper.toCancelOrderRequest(orderId);

        try {
            orderServiceStub.cancelOrder(request);
        } catch (StatusRuntimeException e) {
            throw new OrderServiceUnavailableException(
                    String.format("Order service is unavailable: %s", e.getMessage())
            );
        }
    }
}
