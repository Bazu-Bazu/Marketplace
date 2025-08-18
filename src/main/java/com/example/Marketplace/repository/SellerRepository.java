package com.example.Marketplace.repository;

import com.example.Marketplace.model.Seller;
import com.example.Marketplace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    boolean existsByUser(User user);
    Optional<Seller> findByEmail(String email);

}
