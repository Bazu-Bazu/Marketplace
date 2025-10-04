package com.marketplace.serviceProduct.controller;

import com.marketplace.serviceProduct.dto.request.AddProductRequest;
import com.marketplace.serviceProduct.dto.response.ProductResponse;
import com.marketplace.serviceProduct.exception.HttpServletRequestException;
import com.marketplace.serviceProduct.service.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import com.marketplace.serviceProduct.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final JwtService jwtService;

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(
            HttpServletRequest request,
            @RequestBody List<AddProductRequest> requests) {
        try {
            Long sellerId = jwtService.extractSellerId(request);

            List<ProductResponse> responses = productService.addProducts(sellerId, requests);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(responses);
        } catch (HttpServletRequestException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

}
