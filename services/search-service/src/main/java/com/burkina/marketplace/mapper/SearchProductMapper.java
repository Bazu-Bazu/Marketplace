package com.burkina.marketplace.mapper;

import com.burkina.common.dto.event.marketplace.product.ProductUpdatedEvent;
import com.burkina.marketplace.dto.data.ProductData;
import org.springframework.stereotype.Component;

@Component
public class SearchProductMapper {

    public ProductData toProductData(ProductUpdatedEvent event) {
        return ProductData.builder()
                .productId(event.productId())
                .name(event.name())
                .description(event.description())
                .price(event.price())
                .imageUrl(event.imageUrl())
                .build();
    }
}
