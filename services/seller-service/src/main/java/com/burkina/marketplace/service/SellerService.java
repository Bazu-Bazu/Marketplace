package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.Seller;
import com.burkina.marketplace.domain.repository.SellerRepository;
import com.burkina.marketplace.dto.request.SellerRegisterRequest;
import com.burkina.marketplace.service.event.SellerEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final SellerPhoneService sellerPhoneService;
    private final SellerEmailService sellerEmailService;
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

        newSeller.addPhones(sellerPhoneService.createPhones(request.phones()));
        newSeller.addEmails(sellerEmailService.createEmails(request.emails()));

        Seller savedSeller = sellerRepository.save(newSeller);

        sellerEventPublisher.publishSellerRegistration(savedSeller);

        return savedSeller;
    }
}
