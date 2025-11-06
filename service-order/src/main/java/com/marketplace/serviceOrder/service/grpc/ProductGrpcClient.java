package com.marketplace.serviceOrder.service.grpc;

import com.marketplace.grpc.Product;
import com.marketplace.grpc.ProductServiceGrpc;
import com.marketplace.serviceOrder.dto.grpc.BasketItemValidationResult;
import com.marketplace.serviceOrder.dto.grpc.BasketValidationResult;
import com.marketplace.serviceOrder.entity.BasketItem;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public BasketValidationResult validateBasketItems(List<BasketItem> items) {
        Product.BasketValidationRequest request = createBasketValidationRequest(items);
        Product.BasketValidationResponse response = blockingStub.validateBasketProducts(request);

        return mapToBasketValidationResult(response);
    }

    private Product.BasketValidationRequest createBasketValidationRequest(List<BasketItem> items) {
        Product.BasketValidationRequest.Builder requestBuilder = Product.BasketValidationRequest.newBuilder();

        items.forEach(item -> {
            Product.BasketItemRequest itemRequest = Product.BasketItemRequest.newBuilder()
                    .setProductId(item.getProductId())
                    .setCount(item.getCount())
                    .build();

            requestBuilder.addItems(itemRequest);
        });

        return requestBuilder.build();
    }

    private BasketValidationResult mapToBasketValidationResult(Product.BasketValidationResponse response) {
        Map<Long, BasketItemValidationResult> resultMap = response.getResultsList().stream()
                .collect(Collectors.toMap(
                        Product.ProductValidationResult::getProductId,
                        result -> new BasketItemValidationResult(
                                result.getProductExists(),
                                result.getCurrentPrice(),
                                result.getRequestedCount(),
                                result.getAvailableCount(),
                                result.getIsCountSufficient()
                        )
                ));

        return new BasketValidationResult(resultMap);
    }

}
