package com.burkina.marketplace.controller;

import com.burkina.marketplace.domain.entity.Order;
import com.burkina.marketplace.dto.response.OrderResponse;
import com.burkina.marketplace.mapper.OrderMapper;
import com.burkina.marketplace.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class OrderController {

    private final OrderMapper orderMapper;
    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByUser(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        Page<Order> orders = orderService.getAllByUserId(userId, pageable);

        return ResponseEntity.ok().body(orderMapper.toResponses(orders));
    }
}
