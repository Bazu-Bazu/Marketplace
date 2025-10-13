//package com.marketplace.serviceOrder.config;
//
//import com.marketplace.grpc.ProductServiceGrpc;
//import io.grpc.Grpc;
//import io.grpc.ManagedChannel;
//import net.devh.boot.grpc.client.inject.GrpcClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class GrpcConfig {
//
//    @Bean
//    public ManagedChannel productServiceChannel() {
//        return Grpc.newChannelBuilderForAddress()
//    }
//
//    @Bean
//    @GrpcClient("service-product")
//    public ProductServiceGrpc.ProductServiceStub productServiceStub() {
//        return new ProductServiceGrpc.ProductServiceStub();
//    }
//
//}
