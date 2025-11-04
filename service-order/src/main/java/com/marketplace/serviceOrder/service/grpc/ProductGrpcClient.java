package com.marketplace.serviceOrder.service.grpc;

import com.marketplace.grpc.Product;
import com.marketplace.grpc.ProductServiceGrpc;
import com.marketplace.serviceOrder.dto.grpc.ProductValidationResult;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductGrpcClient {

    @GrpcClient("service-product")
    private ProductServiceGrpc.ProductServiceBlockingStub blockingStub;

    public boolean validateProduct(Long productId) {
        Product.ValidateProductResponse response = getResponse(productId);

        return response.getProductExist();
    }

    public ProductValidationResult validateProductWithDetails(Long productId) {
        Product.ValidateProductResponse response = getResponse(productId);

        return ProductValidationResult.builder()
                .productExist(response.getProductExist())
                .price(response.getPrice())
                .count(response.getCount())
                .build();
    }

    private Product.ValidateProductResponse getResponse(Long productId) {
        Product.ValidateProductRequest request = Product.ValidateProductRequest.newBuilder()
                .setProductId(productId)
                .build();

        return blockingStub.validateProduct(request);
    }

}
