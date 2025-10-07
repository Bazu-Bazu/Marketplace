package com.marketplace.serviceProduct.controller;

import com.marketplace.serviceProduct.dto.request.AddProductRequest;
import com.marketplace.serviceProduct.dto.response.ProductDetailsResponse;
import com.marketplace.serviceProduct.dto.response.ProductShortResponse;
import com.marketplace.serviceProduct.exception.HttpServletRequestException;
import com.marketplace.serviceProduct.service.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import com.marketplace.serviceProduct.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
            String sellerName = jwtService.extractSellerName(request);

            List<ProductDetailsResponse> responses = productService.addProducts(sellerId, sellerName, requests);

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

    @GetMapping("/get-all")
    public ResponseEntity<Page<ProductShortResponse>> getProducts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Page<ProductShortResponse> responses = productService.getProductShort(pageable);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailsResponse> getProductDetail(@PathVariable("productId") Long productId) {
        ProductDetailsResponse response = productService.getProductDetail(productId);

        return ResponseEntity.ok(response);
    }

}
