package com.burkina.marketplace.grpc.client;

import com.burkina.marketplace.dto.grpc.ProductResponse;
import com.burkina.marketplace.exception.ProductNotFoundException;
import com.burkina.marketplace.exception.ProductServiceUnavailableException;
import com.burkina.marketplace.mapper.ProductMapper;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import marketplace.product.Product;
import marketplace.product.ProductServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductGrpcClient {

    private final ProductMapper productMapper;

    @GrpcClient("product-service")
    private ProductServiceGrpc.ProductServiceBlockingStub productServiceStub;

    public ProductResponse getProductForCart(Long productId) {
        Product.GetProductForCartRequest request = productMapper.toProductForCartRequest(productId);

        try {
            Product.GetProductForCartResponse response = productServiceStub.getProductForCart(request);

            return productMapper.toProductResponse(response);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ProductNotFoundException(
                        String.format("Product %d not found", productId)
                );
            }

            throw new ProductServiceUnavailableException(
                    String.format("Product service is unavailable: %s", e.getMessage())
            );
        }
    }
}
