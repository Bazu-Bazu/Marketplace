package com.burkina.marketplace.mapper;

import com.burkina.marketplace.domain.entity.Seller;
import com.burkina.marketplace.dto.response.SellerLongResponse;
import org.springframework.stereotype.Component;

@Component
public class SellerMapper {

    public SellerLongResponse toLongResponse(Seller seller) {
        return SellerLongResponse.from(seller);
    }
}
