package com.burkina.marketplace.grpc.server;

import com.burkina.marketplace.service.OrderService;
import lombok.RequiredArgsConstructor;
import marketplace.order.OrderServiceGrpc;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class OrderGrpcServer extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderService orderService;


}
