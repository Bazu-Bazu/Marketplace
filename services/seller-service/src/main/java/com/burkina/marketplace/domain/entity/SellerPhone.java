package com.burkina.marketplace.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SellerPhone that = (SellerPhone) o;
        return Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(phone);
    }
}
