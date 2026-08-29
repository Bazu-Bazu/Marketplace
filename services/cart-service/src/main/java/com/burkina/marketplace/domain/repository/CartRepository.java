package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("""
            SELECT DISTINCT c
            FROM Cart c
            LEFT JOIN FETCH c.items
            WHERE c.userId = :userId
    """)
    Optional<Cart> findByUserIdWithItems(@Param("userId") Long userId);
}
