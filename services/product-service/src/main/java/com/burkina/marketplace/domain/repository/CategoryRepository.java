package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    @Query("""
            SELECT c
            FROM Category c
            LEFT JOIN FETCH c.parent
            WHERE c.id = :categoryId
    """)
    Optional<Category> findByIdWithParent(@Param("categoryId") Long categoryId);

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
