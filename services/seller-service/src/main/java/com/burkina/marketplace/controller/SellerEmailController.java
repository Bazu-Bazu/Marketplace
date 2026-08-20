package com.burkina.marketplace.controller;

import com.burkina.marketplace.domain.entity.Seller;
import com.burkina.marketplace.dto.request.SellerAddEmailRequest;
import com.burkina.marketplace.dto.response.SellerLongResponse;
import com.burkina.marketplace.mapper.SellerMapper;
import com.burkina.marketplace.service.SellerEmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller/emails")
@RequiredArgsConstructor
public class SellerEmailController {

    private final SellerMapper sellerMapper;
    private final SellerEmailService sellerEmailService;

    @PostMapping
    public ResponseEntity<SellerLongResponse> addEmail(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SellerAddEmailRequest request
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        Seller seller = sellerEmailService.addEmail(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(sellerMapper.toLongResponse(seller));
    }

    @DeleteMapping("/{emailId}")
    public ResponseEntity<Void> deleteEmail(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long emailId
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        sellerEmailService.deleteEmail(userId, emailId);

        return ResponseEntity.noContent().build();
    }
}
