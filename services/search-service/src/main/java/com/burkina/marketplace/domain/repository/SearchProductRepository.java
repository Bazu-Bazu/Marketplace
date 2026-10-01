package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.SearchProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchProductRepository extends JpaRepository<SearchProduct, Long> {

    @Query("""
        SELECT p
        FROM SearchProduct p
        WHERE p.available = true
          AND (
              LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))
          )
    """)
    Page<SearchProduct> search(@Param("query") String query, Pageable pageable);
}
