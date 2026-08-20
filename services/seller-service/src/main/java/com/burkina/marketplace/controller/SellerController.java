package com.burkina.marketplace.controller;

import com.burkina.marketplace.domain.entity.Seller;
import com.burkina.marketplace.dto.request.SellerRegisterRequest;
import com.burkina.marketplace.dto.request.SellerUpdateInfoRequest;
import com.burkina.marketplace.dto.response.SellerLongResponse;
import com.burkina.marketplace.dto.response.SellerShortResponse;
import com.burkina.marketplace.mapper.SellerMapper;
import com.burkina.marketplace.service.SellerCommandService;
import com.burkina.marketplace.validation.validator.SellerValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerMapper sellerMapper;
    private final SellerCommandService sellerService;
    private final SellerValidator sellerValidator;

    @PostMapping
    public ResponseEntity<SellerShortResponse> createSeller(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SellerRegisterRequest request
    ) {
       Long userId = Long.valueOf(jwt.getSubject());
       sellerValidator.validateUserIsNotSeller(userId);

       Seller seller = sellerService.registerSeller(userId, request);

       return ResponseEntity.status(HttpStatus.CREATED).body(sellerMapper.toShortResponse(seller));
    }

    @PatchMapping
    public ResponseEntity<SellerLongResponse> updateSeller(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SellerUpdateInfoRequest request
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        Seller seller = sellerService.updateSellerInfo(userId, request);

        return ResponseEntity.ok().body(sellerMapper.toLongResponse(seller));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        sellerService.delete(userId);

        return ResponseEntity.noContent().build();
    }
}
