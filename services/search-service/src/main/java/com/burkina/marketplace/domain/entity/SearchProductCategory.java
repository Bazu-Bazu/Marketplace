package com.burkina.marketplace.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "search_product_categories",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"product_id", "category_id"}
        )
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private SearchProduct product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private SearchCategory category;

    public void setProduct(SearchProduct product) {
        this.product = product;
    }
}
