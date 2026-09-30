package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.SearchProduct;
import com.burkina.marketplace.domain.entity.SearchProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchProductCategoryRepository extends JpaRepository<SearchProductCategory, Long> {

    List<SearchProductCategory> findAllByProduct(SearchProduct product);
}
