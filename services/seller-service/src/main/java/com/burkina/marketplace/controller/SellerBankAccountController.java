package com.burkina.marketplace.controller;

import com.burkina.marketplace.domain.entity.Seller;
import com.burkina.marketplace.dto.request.SellerAddBankAccountRequest;
import com.burkina.marketplace.dto.response.SellerLongResponse;
import com.burkina.marketplace.mapper.SellerMapper;
import com.burkina.marketplace.service.SellerBankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller/bank-accounts")
@RequiredArgsConstructor
public class SellerBankAccountController {

    private final SellerMapper sellerMapper;
    private final SellerBankAccountService sellerBankAccountService;

    @PostMapping
    public ResponseEntity<SellerLongResponse> addBankAccount(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SellerAddBankAccountRequest request
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        Seller seller = sellerBankAccountService.addBankAccount(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(sellerMapper.toLongResponse(seller));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteBankAccount(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long accountId
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        sellerBankAccountService.deleteBankAccount(userId, accountId);

        return ResponseEntity.noContent().build();
    }
}
