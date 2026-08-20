package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.Seller;
import com.burkina.marketplace.domain.entity.SellerBankAccount;
import com.burkina.marketplace.domain.repository.SellerBankAccountRepository;
import com.burkina.marketplace.dto.request.SellerAddBankAccountRequest;
import com.burkina.marketplace.exception.MaxBankAccountLimitExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SellerBankAccountService {

    private final SellerQueryService sellerService;
    private final SellerBankAccountRepository sellerBankAccountRepository;

    @Transactional
    public Seller addBankAccount(Long userId, SellerAddBankAccountRequest request) {
        Seller seller = sellerService.getSellerWithBankAccountsByUserId(userId);
        Set<SellerBankAccount> bankAccounts = seller.getBankAccounts();

        if (bankAccounts.size() < 5) {
            SellerBankAccount bankAccount = new SellerBankAccount(request.bankName(), request.accountNumber());

            if (!bankAccounts.contains(bankAccount)) {
                seller.addBankAccount(bankAccount);
            }

            return seller;
        }

        throw new MaxBankAccountLimitExceededException(
                String.format("Seller %s has reached the maximum number of bank accounts", seller.getId())
        );
    }

    @Transactional
    public void deleteBankAccount(Long userId, Long accountId) {
        sellerBankAccountRepository.deleteByIdAndUserId(accountId, userId);
    }
}
