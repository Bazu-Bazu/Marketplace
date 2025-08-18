package com.example.Marketplace.controller;

import com.example.Marketplace.dto.AddProductDto;
import com.example.Marketplace.dto.response.ProductResponse;
import com.example.Marketplace.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/add-product")
    public ResponseEntity<ProductResponse> addProduct(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestBody AddProductDto addProductDto) {
        String email = userDetails.getUsername();

        ProductResponse response = productService.addProduct(email, addProductDto);

        return ResponseEntity.ok(response);
    }

}
