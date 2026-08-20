package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.Seller;
import com.burkina.marketplace.domain.entity.SellerEmail;
import com.burkina.marketplace.domain.repository.SellerEmailRepository;
import com.burkina.marketplace.dto.request.SellerAddEmailRequest;
import com.burkina.marketplace.exception.MaxEmailLimitExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SellerEmailService {

    private final SellerQueryService sellerService;
    private final SellerEmailRepository sellerEmailRepository;

    @Transactional
    public Seller addEmail(Long userId, SellerAddEmailRequest request) {
        Seller seller = sellerService.getSellerWithEmailsByUserId(userId);
        Set<SellerEmail> emails = seller.getEmails();

        if (emails.size() < 5) {
            SellerEmail sellerEmail = new SellerEmail(request.email());

            if (!emails.contains(sellerEmail)) {
                seller.addEmail(sellerEmail);
            }

            return seller;
        }

        throw new MaxEmailLimitExceededException(
                String.format("Seller %s has reached the maximum number of emails", seller.getId())
        );
    }

    @Transactional
    public void deleteEmail(Long userId, Long emailId) {
        sellerEmailRepository.deleteByIdAndUserId(emailId, userId);
    }
}
