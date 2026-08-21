package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.Seller;
import com.burkina.marketplace.domain.repository.SellerRepository;
import com.burkina.marketplace.dto.request.*;
import com.burkina.marketplace.mapper.SellerMapper;
import com.burkina.marketplace.service.event.SellerEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerCommandService {

    private final SellerMapper sellerMapper;
    private final SellerRepository sellerRepository;
    private final SellerQueryService sellerQueryService;
    private final SellerEventPublisher sellerEventPublisher;

    @Transactional
    public Seller registerSeller(Long userId, SellerRegisterRequest request) {
        Seller newSeller = Seller.builder()
                .userId(userId)
                .name(request.name())
                .description(request.description())
                .avatarUrl(request.avatarUrl())
                .inn(request.inn())
                .address(request.address())
                .build();

        Seller savedSeller = sellerRepository.save(newSeller);

        sellerEventPublisher.publishSellerRegistration(savedSeller);

        return savedSeller;
    }

    @Transactional
    public Seller updateSellerInfo(Long userId, SellerUpdateInfoRequest request) {
        Seller seller = sellerQueryService.getSellerByUserId(userId);
        seller.update(sellerMapper.toData(request));

        return seller;
    }

    @Transactional
    public void lock(Long sellerId) {
        Seller seller = sellerQueryService.getSellerById(sellerId);

        boolean locked = seller.lock();
        if (locked) {
            sellerEventPublisher.publishSellerLocked(seller);
        }
    }

    @Transactional
    public void unlock(Long sellerId) {
        Seller seller = sellerQueryService.getSellerById(sellerId);

        boolean unlocked = seller.unlock();
        if (unlocked) {
            sellerEventPublisher.publishSellerUnlocked(seller);
        }
    }

    @Transactional
    public void delete(Long userId) {
        Seller seller = sellerQueryService.getSellerByUserId(userId);
        boolean deleted = seller.delete();

        if (deleted) {
            sellerEventPublisher.publishSellerDeleted(seller);
        }
    }
}
