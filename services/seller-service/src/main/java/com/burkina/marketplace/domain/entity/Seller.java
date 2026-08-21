package com.burkina.marketplace.domain.entity;

import com.burkina.marketplace.domain.enums.SellerStatus;
import com.burkina.marketplace.dto.data.SellerData;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.*;

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
    private Set<SellerEmail> emails = new HashSet<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<SellerPhone> phones = new HashSet<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<SellerBankAccount> bankAccounts = new HashSet<>();

    @Column(nullable = false)
    private String description;

    @Column
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SellerStatus status = SellerStatus.ACTIVE;

    @Column(nullable = false, unique = true)
    private String inn;

    @Column(nullable = false)
    private String address;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column
    private Instant deletedAt;

    public void addPhone(SellerPhone phone) {
        this.phones.add(phone);
        phone.setSeller(this);
    }

    public void addEmail(SellerEmail email) {
        this.emails.add(email);
        email.setSeller(this);
    }

    public void addBankAccount(SellerBankAccount bankAccount) {
        this.bankAccounts.add(bankAccount);
        bankAccount.setSeller(this);
    }

    public void update(SellerData data) {
        Optional.ofNullable(data.name())
                .ifPresent(value -> this.name = value);

        Optional.ofNullable(data.description())
                .ifPresent(value -> this.description = value);

        Optional.ofNullable(data.avatarUrl())
                .ifPresent(value -> this.avatarUrl = value);

        Optional.ofNullable(data.inn())
                .ifPresent(value -> this.inn = value);

        Optional.ofNullable(data.address())
                .ifPresent(value -> this.address = value);
    }

    public boolean lock() {
        if (this.status == SellerStatus.ACTIVE) {
            this.status = SellerStatus.LOCKED;

            return true;
        }

        return false;
    }

    public boolean unlock() {
        if (this.status == SellerStatus.LOCKED) {
            this.status = SellerStatus.ACTIVE;

            return true;
        }

        return false;
    }

    public boolean delete() {
        if (this.status != SellerStatus.DELETED) {
            this.status = SellerStatus.DELETED;
            this.deletedAt = Instant.now();

            return true;
        }

        return false;
    }
}
