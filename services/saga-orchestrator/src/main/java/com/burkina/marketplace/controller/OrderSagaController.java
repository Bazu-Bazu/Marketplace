package com.burkina.marketplace.controller;

import com.burkina.marketplace.dto.response.OrderResponse;
import com.burkina.marketplace.service.OrderSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderSagaController {

    private final OrderSagaOrchestrator sagaOrchestrator;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        OrderResponse response = sagaOrchestrator.createOrder(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
