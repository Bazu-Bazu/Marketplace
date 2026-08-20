package com.burkina.marketplace.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "seller_emails")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SellerEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @Column(nullable = false)
    private String email;

    public SellerEmail(String email) {
        this.email = email;
    }

    public void setSeller(Seller seller) {
        if (this.seller == null && seller != null) {
            this.seller = seller;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SellerEmail that = (SellerEmail) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }
}
