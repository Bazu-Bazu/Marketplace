package com.burkina.marketplace.grpc.server;

import com.burkina.marketplace.mapper.OrderMapper;
import com.burkina.marketplace.service.OrderService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import marketplace.order.Order;
import marketplace.order.OrderServiceGrpc;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class OrderGrpcServer extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderMapper orderMapper;
    private final OrderService orderService;

    @Override
    public void createOrder(
            Order.CreateOrderRequest request,
            StreamObserver<Order.CreateOrderResponse> responseObserver
    ) {
        try {
            var order = orderService.createOrder(orderMapper.toCreateOrderData(request));

            responseObserver.onNext(orderMapper.toCreateOrderResponse(order));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void cancelOrder(
            Order.CancelOrderRequest request,
            StreamObserver<Order.CancelOrderResponse> responseObserver
    ) {
        try {
            var order = orderService.cancelOrder(request.getOrderId());

            responseObserver.onNext(orderMapper.toCancelOrderResponse(order));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
