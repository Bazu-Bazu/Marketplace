package com.burkina.marketplace.mapper;

import com.burkina.marketplace.dto.response.ProductResponse;
import marketplace.product.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ProductMapper {

    public Product.ValidateProductsRequest toValidateProductsRequest(List<Long> productIds) {
        return Product.ValidateProductsRequest.newBuilder()
                .addAllProducts(productIds.stream()
                        .map(this::toProductRequest)
                        .toList())
                .build();
    }

    private Product.ProductRequest toProductRequest(Long productId) {
        return Product.ProductRequest.newBuilder()
                .setProductId(productId)
                .build();
    }

    public List<ProductResponse> toProductsResponse(Product.ValidateProductsResponse response) {
        return response.getProductsList().stream()
                .map(this::toProductResponse)
                .toList();
    }

    private ProductResponse toProductResponse(Product.ProductInfo response) {
        return ProductResponse.builder()
                .productId(response.getProductId())
                .exists(response.getExists())
                .available(response.getAvailable())
                .actualPrice(new BigDecimal(response.getPrice()))
                .build();
    }
}
