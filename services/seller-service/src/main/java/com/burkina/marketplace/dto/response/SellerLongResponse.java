package com.burkina.marketplace.dto.response;

import com.burkina.marketplace.domain.entity.Seller;
import com.burkina.marketplace.domain.entity.SellerBankAccount;
import com.burkina.marketplace.domain.entity.SellerEmail;
import com.burkina.marketplace.domain.entity.SellerPhone;
import com.burkina.marketplace.domain.enums.SellerStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record SellerLongResponse(
        Long id,
        Long userId,
        String name,
        String description,
        String avatarUrl,
        SellerStatus status,
        String inn,
        String address,
        Instant createdAt,
        List<Email> emails,
        List<Phone> phones,
        List<BankAccount> bankAccounts
) {

    private record Phone(
            Long id,
            String phone
    ) {}

    private record Email(
            Long id,
            String email
    ) {}

    private record BankAccount(
            Long id,
            String bankName,
            String accountNumber
    ) {}

    public static SellerLongResponse from(Seller seller) {
        if (seller == null) {
            return null;
        }

        return new SellerLongResponse(
                seller.getId(),
                seller.getUserId(),
                seller.getName(),
                seller.getDescription(),
                seller.getAvatarUrl(),
                seller.getStatus(),
                seller.getInn(),
                seller.getAddress(),
                seller.getCreatedAt(),
                mapEmails(seller.getEmails().stream().toList()),
                mapPhones(seller.getPhones().stream().toList()),
                mapBankAccounts(seller.getBankAccounts().stream().toList())
        );
    }

    private static List<Email> mapEmails(List<SellerEmail> sellerEmails) {
        if (sellerEmails == null) return new ArrayList<>();

        return sellerEmails.stream()
                .map(email -> new Email(email.getId(), email.getEmail()))
                .toList();
    }

    private static List<Phone> mapPhones(List<SellerPhone> sellerPhones) {
        if (sellerPhones == null) return new ArrayList<>();

        return sellerPhones.stream()
                .map(phone -> new Phone(phone.getId(), phone.getPhone()))
                .toList();
    }

    private static List<BankAccount> mapBankAccounts(List<SellerBankAccount> bankAccounts) {
        if (bankAccounts == null) return new ArrayList<>();

        return bankAccounts.stream()
                .map(account -> new BankAccount(account.getId(), account.getBankName(), account.getAccountNumber()))
                .toList();
    }
}
