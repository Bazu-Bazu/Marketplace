package com.marketplace.serviceOrder.controller;

import com.marketplace.serviceOrder.dto.request.AddItemRequest;
import com.marketplace.serviceOrder.dto.response.BasketItemResponse;
import com.marketplace.serviceOrder.exception.HttpServletRequestException;
import com.marketplace.serviceOrder.service.BasketItemService;
import com.marketplace.serviceOrder.service.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/basket-item")
@RequiredArgsConstructor
public class BasketItemController {

    private final BasketItemService basketItemService;
    private final JwtService jwtService;

    @PostMapping("/add")
    public ResponseEntity<?> addItem(
            HttpServletRequest httpServletRequest,
            @RequestBody AddItemRequest request)
    {
        try {
            Long userId = jwtService.extractUserId(httpServletRequest);

            BasketItemResponse response = basketItemService.addItem(userId, request);

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
