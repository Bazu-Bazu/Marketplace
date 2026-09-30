package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.SearchProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchProductRepository extends JpaRepository<SearchProduct, Long> {}
