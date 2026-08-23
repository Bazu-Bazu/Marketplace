package com.burkina.marketplace.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "product_category",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_category",
                        columnNames = {"product_id", "category_id"}
                )
        }
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
