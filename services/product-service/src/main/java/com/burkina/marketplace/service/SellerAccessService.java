package com.burkina.marketplace.service;

import com.burkina.common.dto.event.SellerDeletedEvent;
import com.burkina.common.dto.event.SellerLockedEvent;
import com.burkina.common.dto.event.SellerRegisteredEvent;
import com.burkina.common.dto.event.SellerUnlockedEvent;
import com.burkina.marketplace.domain.entity.SellerAccess;
import com.burkina.marketplace.domain.repository.SellerAccessRepository;
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
}
