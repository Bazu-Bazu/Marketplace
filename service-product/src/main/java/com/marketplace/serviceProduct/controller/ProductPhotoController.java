package com.marketplace.serviceProduct.controller;

import com.marketplace.serviceProduct.dto.response.ProductPhotoResponse;
import com.marketplace.serviceProduct.service.ProductPhotoService;
import com.marketplace.serviceProduct.service.ProductService;
import com.marketplace.serviceProduct.service.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/product-photo")
@RequiredArgsConstructor
public class ProductPhotoController {

    private final ProductPhotoService productPhotoService;
    private final ProductService productService;
    private final JwtService jwtService;

    @PostMapping("/add")
    public ResponseEntity<?> addProductPhotos(
            HttpServletRequest request,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("productId") Long productId)
    {
        try {
            Long sellerId = jwtService.extractSellerId(request);

            productService.validateProductOwnership(sellerId, productId);

            List<ProductPhotoResponse> responses = productPhotoService.addProductPhotos(files, productId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(responses);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable("fileId") String fileId) {
        try {
            return productPhotoService.downloadPhoto(fileId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

}
