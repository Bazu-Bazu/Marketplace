package com.burkina.marketplace.domain.entity;

import com.burkina.marketplace.domain.enums.SagaStatus;
import com.burkina.marketplace.domain.enums.SagaStep;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "order_sagas")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SagaStatus status = SagaStatus.STARTED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStep currentStep;

    @Column
    private Long orderId;

    @Column
    private Long reservationId;

    @Column
    private Long paymentId;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public void moveTo(SagaStep step) {
        this.currentStep = step;
        this.status = SagaStatus.PROCESSING;
    }

    public void startCompensation() {
        this.status = SagaStatus.COMPENSATING;
    }

    public void complete() {
        this.currentStep = SagaStep.COMPLETED;
        this.status = SagaStatus.COMPLETED;
    }

    public void fail() {
        this.status = SagaStatus.FAILED;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}