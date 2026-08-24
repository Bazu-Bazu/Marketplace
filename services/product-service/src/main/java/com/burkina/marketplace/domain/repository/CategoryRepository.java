package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    @EntityGraph(attributePaths = "parent")
    Optional<Category> findByIdWithParent(Long id);

    @Query("""
            SELECT c
            FROM Category c
            WHERE c.id IN :categoryIds
              AND NOT EXISTS (
                  SELECT pc.id
                  FROM ProductCategory pc
                  WHERE pc.category.id = c.id
                    AND pc.product.id = :productId
              )
            """)
    Set<Category> findExistingNotAssignedToProduct(
            @Param("categoryIds") Set<Long> categoryIds,
            @Param("productId") Long productId
    );
}
