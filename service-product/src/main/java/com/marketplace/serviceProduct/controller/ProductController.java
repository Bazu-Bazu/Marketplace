package com.marketplace.serviceProduct.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.serviceProduct.dto.request.AddProductRequest;
import com.marketplace.serviceProduct.dto.response.ProductResponse;
import com.marketplace.serviceProduct.service.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.marketplace.serviceProduct.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final JwtService jwtService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> addProduct(
            @RequestPart(value = "product") @Valid String product,
            @RequestPart(value = "images", required = false) MultipartFile[] imageFiles,
            HttpServletRequest httpRequest) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        AddProductRequest request = objectMapper.readValue(product, AddProductRequest.class);

        Long sellerId = jwtService.extractSellerId(httpRequest);

        List<MultipartFile> files = imageFiles != null ?
                Arrays.asList(imageFiles) : Collections.emptyList();

        ProductResponse response = productService.addProduct(sellerId, request, files);

        return ResponseEntity.ok(response);
    }

}
