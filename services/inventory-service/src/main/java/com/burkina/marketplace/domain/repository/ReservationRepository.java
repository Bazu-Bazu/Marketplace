package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.Reservation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @EntityGraph(attributePaths = {"items"})
    Optional<Reservation> findBySagaId(Long sagaId);

    @EntityGraph(attributePaths = {"items"})
    Optional<Reservation> findById(Long reservationId);
}
