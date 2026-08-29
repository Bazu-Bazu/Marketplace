package com.burkina.marketplace.grpc.server;

import com.burkina.marketplace.exception.ProductNotFoundException;
import com.burkina.marketplace.mapper.ProductMapper;
import com.burkina.marketplace.service.ProductQueryService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import marketplace.product.Product;
import marketplace.product.ProductServiceGrpc;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductMapper productMapper;
    private final ProductQueryService productQueryService;

    @Override
    public void getProductForCart(
            Product.GetProductForCartRequest request,
            StreamObserver<Product.GetProductForCartResponse> responseObserver
    ) {
        try {
            var product = productQueryService.getProductById(request.getProductId());

            responseObserver.onNext(productMapper.toProductForCartResponse(product));
            responseObserver.onCompleted();
        } catch (ProductNotFoundException e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }
}
