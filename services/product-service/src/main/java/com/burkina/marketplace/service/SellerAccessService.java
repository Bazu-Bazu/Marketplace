package com.burkina.marketplace.service;

import com.burkina.common.dto.event.marketplace.seller.*;
import com.burkina.marketplace.domain.entity.SellerAccess;
import com.burkina.marketplace.domain.repository.SellerAccessRepository;
import com.burkina.marketplace.exception.AuthorizationException;
import com.burkina.marketplace.exception.SellerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerAccessService {

    private final SellerAccessRepository sellerAccessRepository;

    @Transactional
    public void createSellerAccess(SellerRegisteredEvent event) {
        SellerAccess sellerAccess = SellerAccess.builder()
                .sellerId(event.sellerId())
                .userId(event.userId())
                .build();

        sellerAccessRepository.save(sellerAccess);
    }

    @Transactional
    public void lockSeller(SellerLockedEvent event) {
        sellerAccessRepository.lockSeller(event.sellerId());
    }

    @Transactional
    public void unlockSeller(SellerUnlockedEvent event) {
        sellerAccessRepository.unlockSeller(event.sellerId());
    }

    @Transactional
    public void deleteSeller(SellerDeletedEvent event) {
        sellerAccessRepository.deleteSeller(event.sellerId());
    }

    @Transactional(readOnly = true)
    public Long getActiveSellerIdByUserId(Long userId) {
        SellerAccess sellerAccess = sellerAccessRepository.findByUserId(userId)
                .orElseThrow(() -> new SellerNotFoundException(
                        String.format("Seller not found for user with id: %d", userId)
                ));

        if (!sellerAccess.isActive()) {
            throw new AuthorizationException(
                    String.format("Seller with id: %d is not active", sellerAccess.getSellerId())
            );
        }

        return sellerAccess.getSellerId();
    }
}
