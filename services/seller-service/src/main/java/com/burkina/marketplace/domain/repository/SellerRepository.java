package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    boolean existsByUserId(Long userId);
}
