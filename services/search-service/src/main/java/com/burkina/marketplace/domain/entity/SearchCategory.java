package com.burkina.marketplace.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "search_categories")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCategory {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private SearchCategory parent;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    public void setParent(SearchCategory parent) {
        this.parent = parent;
    }

    public void inactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }
}
