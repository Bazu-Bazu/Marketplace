package com.burkina.marketplace.mapper;

import com.burkina.common.dto.event.marketplace.seller.SellerDeletedEvent;
import com.burkina.common.dto.event.marketplace.seller.SellerLockedEvent;
import com.burkina.common.dto.event.marketplace.seller.SellerRegisteredEvent;
import com.burkina.common.dto.event.marketplace.seller.SellerUnlockedEvent;
import com.burkina.marketplace.domain.entity.Seller;
import com.burkina.marketplace.dto.data.SellerData;
import com.burkina.marketplace.dto.request.SellerUpdateInfoRequest;
import com.burkina.marketplace.dto.response.SellerLongResponse;
import com.burkina.marketplace.dto.response.SellerShortResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SellerMapper {

    public SellerLongResponse toLongResponse(Seller seller) {
        return SellerLongResponse.from(seller);
    }

    public SellerShortResponse toShortResponse(Seller seller) {
        return SellerShortResponse.builder()
                .id(seller.getId())
                .userId(seller.getUserId())
                .name(seller.getName())
                .avatarUrl(seller.getAvatarUrl())
                .description(seller.getDescription())
                .address(seller.getAddress())
                .inn(seller.getInn())
                .status(seller.getStatus())
                .createdAt(seller.getCreatedAt())
                .build();
    }

    public SellerData toData(SellerUpdateInfoRequest request) {
        return SellerData.builder()
                .name(request.name())
                .description(request.description())
                .address(request.address())
                .avatarUrl(request.avatarUrl())
                .inn(request.inn())
                .build();
    }

    public SellerRegisteredEvent toSellerRegisteredEvent(Seller seller) {
        return SellerRegisteredEvent.builder()
                .sellerId(seller.getId())
                .userId(seller.getUserId())
                .name(seller.getName())
                .createdAt(seller.getCreatedAt())
                .build();
    }

    public SellerLockedEvent toSellerLockedEvent(Seller seller) {
        return SellerLockedEvent.builder()
                .sellerId(seller.getId())
                .userId(seller.getUserId())
                .lockedAt(Instant.now())
                .build();
    }

    public SellerUnlockedEvent toSellerUnlockedEvent(Seller seller) {
        return SellerUnlockedEvent.builder()
                .sellerId(seller.getId())
                .userId(seller.getUserId())
                .unlockedAt(Instant.now())
                .build();
    }

    public SellerDeletedEvent toSellerDeletedEvent(Seller seller) {
        return SellerDeletedEvent.builder()
                .sellerId(seller.getId())
                .userId(seller.getUserId())
                .deletedAt(seller.getDeletedAt())
                .build();
    }
}
