package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.Seller;
import com.burkina.marketplace.domain.entity.SellerPhone;
import com.burkina.marketplace.domain.repository.SellerPhoneRepository;
import com.burkina.marketplace.dto.request.SellerAddPhoneRequest;
import com.burkina.marketplace.exception.MaxPhoneLimitExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SellerPhoneService {

    private final SellerQueryService sellerService;
    private final SellerPhoneRepository sellerPhoneRepository;

    @Transactional
    public Seller addPhone(Long userId, SellerAddPhoneRequest request) {
        Seller seller = sellerService.getSellerWithPhonesByUserId(userId);
        Set<SellerPhone> phones = seller.getPhones();

        if (phones.size() < 5) {
            SellerPhone sellerPhone = new SellerPhone(request.phone());

            if (!phones.contains(sellerPhone)) {
                seller.addPhone(sellerPhone);
            }

            return seller;
        }

        throw new MaxPhoneLimitExceededException(
                String.format("Seller %s has reached the maximum number of phones", seller.getId())
        );
    }

    @Transactional
    public void deletePhone(Long userId, Long phoneId) {
        sellerPhoneRepository.deleteByIdAndUserId(phoneId, userId);
    }
}
