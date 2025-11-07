package com.marketplace.serviceProduct.service.grpc;

import com.marketplace.grpc.Product;
import com.marketplace.grpc.ProductServiceGrpc;
import com.marketplace.serviceProduct.exception.ProductException;
import com.marketplace.serviceProduct.service.ProductService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

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
            productService.findProductById(request.getProductId());

            sendValidateProductResponse(responseObserver, true);
        } catch (ProductException e) {
            sendValidateProductResponse(responseObserver, false);
        }
    }

    private void sendValidateProductResponse(
            StreamObserver<Product.ValidateProductResponse> responseObserver,
            boolean productExist)
    {
        Product.ValidateProductResponse response = Product.ValidateProductResponse.newBuilder()
                .setProductExist(productExist)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void validateBasketProducts(Product.BasketValidationRequest request,
                                       StreamObserver<Product.BasketValidationResponse> responseObserver)
    {
        Product.BasketValidationResponse.Builder responseBuilder = Product.BasketValidationResponse.newBuilder();

        for (Product.BasketItemRequest basketItem : request.getItemsList()) {
            Product.ProductValidationResult validationResult = validateProductForBasket(basketItem);
            responseBuilder.addResults(validationResult);
        }

        Product.BasketValidationResponse response = responseBuilder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private Product.ProductValidationResult validateProductForBasket(Product.BasketItemRequest basketItem) {
        long productId = basketItem.getProductId();
        int requestedCount = basketItem.getCount();

        try {
            var product = productService.findProductById(productId);
            return createFoundProductResult(product, requestedCount);
        } catch (Exception e) {
            return createNotFoundProductResult(productId, requestedCount);
        }
    }

    private Product.ProductValidationResult createNotFoundProductResult(long productId, int requestedCount) {
        return Product.ProductValidationResult.newBuilder()
                .setProductId(productId)
                .setProductExists(false)
                .setRequestedCount(requestedCount)
                .setAvailableCount(0)
                .setIsCountSufficient(false)
                .setCurrentPrice(0)
                .build();
    }

    private Product.ProductValidationResult createFoundProductResult(
            com.marketplace.serviceProduct.entity.Product product, int requestedCount)
    {
        return Product.ProductValidationResult.newBuilder()
                .setProductId(product.getId())
                .setProductExists(true)
                .setRequestedCount(requestedCount)
                .setAvailableCount(product.getCount())
                .setIsCountSufficient(requestedCount <= product.getCount())
                .setCurrentPrice(product.getPrice())
                .build();
    }

}
