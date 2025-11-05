package com.marketplace.serviceOrder.service.grpc;

import com.marketplace.grpc.Product;
import com.marketplace.grpc.ProductServiceGrpc;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductGrpcClient {

    @GrpcClient("service-product")
    private ProductServiceGrpc.ProductServiceBlockingStub blockingStub;

    public boolean validateProduct(Long productId) {
        Product.ValidateProductRequest request = Product.ValidateProductRequest.newBuilder()
                .setProductId(productId)
                .build();

        Product.ValidateProductResponse response = blockingStub.validateProduct(request);

        return response.getProductExist();
    }

}
