package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.Seller;
import com.burkina.marketplace.domain.repository.SellerRepository;
import com.burkina.marketplace.exception.SellerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerQueryService {

    private final SellerRepository sellerRepository;

    @Transactional(readOnly = true)
    public Seller getSellerByUserId(Long userId) {
        return sellerRepository.findByUserId(userId)
                .orElseThrow(() -> new SellerNotFoundException(
                        String.format("Seller with userId %d not found", userId)
                ));
    }

    @Transactional(readOnly = true)
    public Seller getSellerById(Long id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException(
                        String.format("Seller by id %d not found", id)
                ));
    }

    @Transactional(readOnly = true)
    public Seller getSellerWithPhonesByUserId(Long userId) {
        return sellerRepository.findWithPhonesByUserId(userId)
                .orElseThrow(() -> new SellerNotFoundException(
                        String.format("Seller with userId %d not found", userId)
                ));
    }

    @Transactional(readOnly = true)
    public Seller getSellerWithEmailsByUserId(Long userId) {
        return sellerRepository.findWithEmailsByUserId(userId)
                .orElseThrow(() -> new SellerNotFoundException(
                        String.format("Seller with userId %d not found", userId)
                ));
    }

    @Transactional(readOnly = true)
    public Seller getSellerWithBankAccountsByUserId(Long userId) {
        return sellerRepository.findWithBankAccountsByUserId(userId)
                .orElseThrow(() -> new SellerNotFoundException(
                        String.format("Seller with userId %d not found", userId)
                ));
    }
}
