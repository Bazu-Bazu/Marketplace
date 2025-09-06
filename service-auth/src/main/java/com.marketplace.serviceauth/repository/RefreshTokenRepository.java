package com.marketplace.serviceauth.repository;

import com.marketplace.serviceauth.entity.RefreshToken;
import com.marketplace.serviceauth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    void deleteAllByUser(User user);

}
