package com.burkina.marketplace.controller;

import com.burkina.marketplace.domain.entity.Seller;
import com.burkina.marketplace.dto.response.SellerLongResponse;
import com.burkina.marketplace.mapper.SellerMapper;
import com.burkina.marketplace.service.SellerCommandService;
import com.burkina.marketplace.service.SellerQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/sellers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSellersController {

    private final SellerMapper sellerMapper;
    private final SellerQueryService sellerQueryService;
    private final SellerCommandService sellerCommandService;

    @GetMapping("/{sellerId}")
    public ResponseEntity<SellerLongResponse> getSeller(@PathVariable Long sellerId) {
        Seller seller = sellerQueryService.getSellerByUserId(sellerId);

        return ResponseEntity.ok().body(sellerMapper.toLongResponse(seller));
    }

    @PatchMapping("/{sellerId}/lock")
    public ResponseEntity<Void> lockSeller(@PathVariable Long sellerId) {
        sellerCommandService.lock(sellerId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{sellerId}/unlock")
    public ResponseEntity<Void> unlockSeller(@PathVariable Long sellerId) {
        sellerCommandService.unlock(sellerId);

        return ResponseEntity.noContent().build();
    }
}
