package com.burkina.marketplace.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "seller_phones")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SellerPhone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @Column(nullable = false)
    private String phone;

    public SellerPhone(String phone) {
        this.phone = phone;
    }

    public void setSeller(Seller seller) {
        if (this.seller == null && seller != null) {
            this.seller = seller;
        }
    }
}
