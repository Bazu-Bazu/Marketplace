package marketplace.serviceproduct.service;

import lombok.RequiredArgsConstructor;
import marketplace.serviceproduct.dto.request.AddProductRequest;
import marketplace.serviceproduct.dto.response.ProductResponse;
import marketplace.serviceproduct.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse addProduct(String email, AddProductRequest request) {

    }

    public ProductResponse getProduct() {

    }

    @Transactional
    public ProductResponse updateProduct() {

    }

    @Transactional
    public ProductResponse deleteProduct() {

    }

}
