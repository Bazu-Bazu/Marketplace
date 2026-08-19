package com.burkina.marketplace.controller;

import com.burkina.marketplace.domain.entity.Seller;
import com.burkina.marketplace.dto.request.SellerRegisterRequest;
import com.burkina.marketplace.dto.response.SellerLongResponse;
import com.burkina.marketplace.mapper.SellerMapper;
import com.burkina.marketplace.service.SellerService;
import com.burkina.marketplace.validation.validator.SellerValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerMapper sellerMapper;
    private final SellerService sellerService;
    private final SellerValidator sellerValidator;

    @PostMapping
    public ResponseEntity<SellerLongResponse> createSeller(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SellerRegisterRequest request
    ) {
       Long userId = Long.valueOf(jwt.getSubject());
       sellerValidator.validateUserIsNotSeller(userId);

       Seller seller = sellerService.registerSeller(userId, request);

       return ResponseEntity.status(HttpStatus.CREATED).body(sellerMapper.toLongResponse(seller));
    }
}
