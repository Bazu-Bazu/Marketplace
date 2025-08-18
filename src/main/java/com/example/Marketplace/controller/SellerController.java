package com.example.Marketplace.controller;

import com.example.Marketplace.dto.RegisterSellerDto;
import com.example.Marketplace.dto.response.SellerResponse;
import com.example.Marketplace.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/register")
    public ResponseEntity<SellerResponse> registerSeller(@AuthenticationPrincipal UserDetails userDetails,
                                                         @RequestBody RegisterSellerDto registerSellerDto) {
        String email = userDetails.getUsername();

        SellerResponse response = sellerService.registerSeller(email, registerSellerDto.getName());

        return ResponseEntity.ok(response);
    }

}
