package com.burkina.marketplace.domain.entity;

import com.burkina.marketplace.domain.enums.SellerStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sellers")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SellerEmail> emails = new ArrayList<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SellerPhone> phones = new ArrayList<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SellerBankAccount> bankAccounts = new ArrayList<>();

    @Column(nullable = false)
    private String description;

    @Column
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SellerStatus status = SellerStatus.NEW;

    @Column(nullable = false, unique = true)
    private String inn;

    @Column(nullable = false, unique = true)
    private String address;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column
    private Instant deletedAt;

    public void addPhones(List<SellerPhone> phones) {
        phones.forEach(this::addPhone);
    }

    private void addPhone(SellerPhone phone) {
        if (this.phones == null) {
            this.phones = new ArrayList<>();
        }

        this.phones.add(phone);
        phone.setSeller(this);
    }

    public void addEmails(List<SellerEmail> emails) {
        emails.forEach(this::addEmail);
    }

    private void addEmail(SellerEmail email) {
        if (this.emails == null) {
            this.emails = new ArrayList<>();
        }

        this.emails.add(email);
        email.setSeller(this);
    }

    public void addBankAccounts(List<SellerBankAccount> bankAccounts) {
        bankAccounts.forEach(this::addBankAccount);
    }

    private void addBankAccount(SellerBankAccount bankAccount) {
        if (this.bankAccounts == null) {
            this.bankAccounts = new ArrayList<>();
        }

        this.bankAccounts.add(bankAccount);
        bankAccount.setSeller(this);
    }
}
