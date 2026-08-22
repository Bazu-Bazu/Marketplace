package com.burkina.marketplace.domain.entity;

import com.burkina.marketplace.domain.enums.SellerStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sellers")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SellerAccess {

    @Id
    private Long sellerId;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SellerStatus status = SellerStatus.ACTIVE;
}
