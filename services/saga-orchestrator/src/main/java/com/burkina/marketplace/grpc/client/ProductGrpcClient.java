package com.burkina.marketplace.grpc.client;

import com.burkina.marketplace.dto.response.ProductResponse;
import com.burkina.marketplace.exception.ProductServiceUnavailableException;
import com.burkina.marketplace.mapper.ProductMapper;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import marketplace.product.Product;
import marketplace.product.ProductServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductGrpcClient {

    private final ProductMapper productMapper;

    @GrpcClient("product-service")
    private ProductServiceGrpc.ProductServiceBlockingStub productServiceStub;

    public List<ProductResponse> getProducts(List<Long> productIds) {
        Product.ValidateProductsRequest request = productMapper.toValidateProductsRequest(productIds);

        try {
            Product.ValidateProductsResponse response = productServiceStub.validateProducts(request);

            return productMapper.toProductsResponse(response);
        } catch (StatusRuntimeException e) {
            throw new ProductServiceUnavailableException(
                    String.format("Product service is unavailable: %s", e.getMessage())
            );
        }
    }
}
