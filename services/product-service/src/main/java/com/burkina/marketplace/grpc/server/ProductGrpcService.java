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

import java.util.List;

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
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void validateProducts(
            Product.ValidateProductsRequest request,
            StreamObserver<Product.ValidateProductsResponse> responseObserver
    ) {
        try {
            List<Long> productIds = request.getProductsList().stream()
                    .map(Product.ProductRequest::getProductId)
                    .distinct()
                    .toList();

            List<com.burkina.marketplace.domain.entity.Product> products =
                    productQueryService.getProductsById(productIds);

            responseObserver.onNext(productMapper.toValidateProductsResponse(productIds, products));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
