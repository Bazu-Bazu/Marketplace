package com.marketplace.serviceProduct.controller;

import lombok.RequiredArgsConstructor;
import com.marketplace.serviceProduct.service.ProductService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

//    @PostMapping("/add")
//    public ResponseEntity<ProductResponse> addProduct(@RequestBody AddProductRequest request) {
//        ProductResponse response = productService.addProduct(request);
//    }

}
