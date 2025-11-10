package com.marketplace.serviceOrder.controller;

import com.marketplace.serviceOrder.dto.request.SetOrderItemRatingRequest;
import com.marketplace.serviceOrder.dto.response.OrderItemResponse;
import com.marketplace.serviceOrder.entity.Order;
import com.marketplace.serviceOrder.entity.OrderItem;
import com.marketplace.serviceOrder.exception.AccessRightsException;
import com.marketplace.serviceOrder.exception.HttpServletRequestException;
import com.marketplace.serviceOrder.service.OrderItemService;
import com.marketplace.serviceOrder.service.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order-item")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;
    private final JwtService jwtService;

    @PatchMapping("/rating")
    public ResponseEntity<?> sendOrderItemRating(
            HttpServletRequest httpServletRequest,
            @RequestBody SetOrderItemRatingRequest request)
    {
        try {
            Long userId = jwtService.extractUserId(httpServletRequest);

            validateUserAndOrderItem(userId, request.getOrderItemId());

            OrderItemResponse response = orderItemService.setOrderItemRating(request);

            return ResponseEntity.ok(response);
        } catch (HttpServletRequestException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } catch (AccessRightsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    private void validateUserAndOrderItem(Long userId, Long orderItemId) {
        OrderItem orderItem = orderItemService.findOrderItemById(orderItemId);
        Order order = orderItem.getOrder();

        if (!userId.equals(order.getUserId())) {
            throw new AccessRightsException("Access rights exception.");
        }
    }

}
