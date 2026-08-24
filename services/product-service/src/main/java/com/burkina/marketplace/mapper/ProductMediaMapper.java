package com.burkina.marketplace.mapper;

import com.burkina.marketplace.domain.entity.ProductMedia;
import com.burkina.marketplace.dto.response.ProductMediaResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMediaMapper {

    public ProductMediaResponse toResponse(ProductMedia productMedia) {
        return ProductMediaResponse.builder()
                .id(productMedia.getId())
                .url(productMedia.getUrl())
                .build();
    }
}
