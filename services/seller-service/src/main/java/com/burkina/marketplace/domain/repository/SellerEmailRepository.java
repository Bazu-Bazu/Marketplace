package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.SellerEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerEmailRepository extends JpaRepository<SellerEmail, Long> {

    @Modifying
    @Query("""
        DELETE FROM SellerEmail se
        WHERE se.id = :id
        AND se.seller.userId = :userId
    """)
    void deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
