package com.burkina.marketplace.controller;

import com.burkina.marketplace.domain.entity.Seller;
import com.burkina.marketplace.dto.request.SellerAddPhoneRequest;
import com.burkina.marketplace.dto.response.SellerLongResponse;
import com.burkina.marketplace.mapper.SellerMapper;
import com.burkina.marketplace.service.SellerPhoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller/phones")
@RequiredArgsConstructor
public class SellerPhoneController {

    private final SellerMapper sellerMapper;
    private final SellerPhoneService sellerPhoneService;

    @PostMapping
    public ResponseEntity<SellerLongResponse> addPhone(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SellerAddPhoneRequest request
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        Seller seller = sellerPhoneService.addPhone(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(sellerMapper.toLongResponse(seller));
    }

    @DeleteMapping("/{phoneId}")
    public ResponseEntity<Void> deletePhone(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long phoneId
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        sellerPhoneService.deletePhone(userId, phoneId);

        return ResponseEntity.noContent().build();
    }
}
