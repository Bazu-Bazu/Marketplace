package com.marketplace.serviceProduct.service.grpc;

import com.marketplace.grpc.Product;
import com.marketplace.grpc.ProductServiceGrpc;
import com.marketplace.serviceProduct.exception.ProductException;
import com.marketplace.serviceProduct.service.ProductService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@GrpcService
@RequiredArgsConstructor
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService productService;

    @Override
    public void validateProduct(
            Product.ValidateProductRequest request,
            StreamObserver<Product.ValidateProductResponse> responseObserver)
    {
        try {
            Map<String, Integer> map = productService.validateProduct(request.getProductId());

            sendValidateProductResponse(responseObserver, true, map.get("price"), map.get("count"));
        } catch (ProductException e) {
            sendValidateProductResponse(responseObserver, false, 0, 0);
        }
    }

    private void sendValidateProductResponse(
            StreamObserver<Product.ValidateProductResponse> responseObserver,
            boolean productExist, int price, int count)
    {
        Product.ValidateProductResponse response = Product.ValidateProductResponse.newBuilder()
                .setProductExist(productExist)
                .setPrice(price)
                .setCount(count)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
