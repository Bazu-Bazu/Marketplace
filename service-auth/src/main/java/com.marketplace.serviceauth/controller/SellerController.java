package com.marketplace.serviceauth.controller;

import com.marketplace.serviceauth.dto.request.RegisterSellerRequest;
import com.marketplace.serviceauth.dto.request.UpdateSellerRequest;
import com.marketplace.serviceauth.dto.response.SellerResponse;
import com.marketplace.serviceauth.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/register")
    public ResponseEntity<?> registerSeller(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody RegisterSellerRequest request)
    {
        try {
            String email = userDetails.getUsername();

            SellerResponse response = sellerService.registerSeller(email, request);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateSeller(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateSellerRequest request)
    {
        try {
            String email = userDetails.getUsername();

            SellerResponse response = sellerService.updateSeller(email, request);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getSeller(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();

            SellerResponse response = sellerService.getSeller(email);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteSeller(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();

            sellerService.deleteSeller(email);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

}
