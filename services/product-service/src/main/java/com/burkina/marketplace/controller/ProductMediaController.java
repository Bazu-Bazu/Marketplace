package com.burkina.marketplace.controller;

import com.burkina.marketplace.domain.entity.ProductMedia;
import com.burkina.marketplace.dto.request.AddProductMediaRequest;
import com.burkina.marketplace.dto.response.ProductMediaResponse;
import com.burkina.marketplace.mapper.ProductMediaMapper;
import com.burkina.marketplace.service.ProductMediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductMediaController {

    private final ProductMediaMapper productMediaMapper;
    private final ProductMediaService productMediaService;

    @PostMapping("/{productId}/medias")
    public ResponseEntity<ProductMediaResponse> addProductMedia(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId,
            @Valid @RequestBody AddProductMediaRequest request
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        ProductMedia productMedia = productMediaService.addProductMedia(userId, productId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(productMediaMapper.toResponse(productMedia));
    }

    @DeleteMapping("/{productId}/medias/{mediaId}")
    public ResponseEntity<Void> removeProductMedia(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId,
            @PathVariable Long mediaId
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        productMediaService.removeProductMedia(userId, productId, mediaId);

        return ResponseEntity.noContent().build();
    }
}
