package com.marketplace.serviceOrder.controller;

import com.marketplace.serviceOrder.dto.response.OrderResponse;
import com.marketplace.serviceOrder.exception.HttpServletRequestException;
import com.marketplace.serviceOrder.service.OrderService;
import com.marketplace.serviceOrder.service.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(
            HttpServletRequest httpServletRequest,
            @RequestParam(name = "address") String address)
    {
        try {
            Long userId = jwtService.extractUserId(httpServletRequest);

            OrderResponse response = orderService.createOrder(userId, address);

            return ResponseEntity.ok(response);
        } catch (HttpServletRequestException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

}
