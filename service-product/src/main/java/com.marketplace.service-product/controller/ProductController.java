package marketplace.serviceproduct.controller;

import lombok.RequiredArgsConstructor;
import marketplace.serviceproduct.dto.request.AddProductRequest;
import marketplace.serviceproduct.dto.response.ProductResponse;
import marketplace.serviceproduct.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<ProductResponse> addProduct(@RequestBody AddProductRequest request) {
        ProductResponse response = productService.addProduct(request)
    }

}
