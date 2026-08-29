package com.burkina.marketplace.mapper;

import com.burkina.marketplace.dto.grpc.ProductResponse;
import marketplace.product.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductMapper {

    public ProductResponse toProductResponse(Product.GetProductForCartResponse response) {
        return ProductResponse.builder()
                .productId(response.getProductId())
                .price(new BigDecimal(response.getPrice()))
                .available(response.getAvailable())
                .build();
    }

    public Product.GetProductForCartRequest toProductForCartRequest(Long productId) {
        return Product.GetProductForCartRequest.newBuilder()
                .setProductId(productId)
                .build();
    }
}
