package marketplace.serviceproduct.controller;

import com.marketplace.serviceProduct.controller.ProductController;
import com.marketplace.serviceProduct.dto.request.AddProductRequest;
import com.marketplace.serviceProduct.dto.response.ProductDetailsResponse;
import com.marketplace.serviceProduct.dto.response.ProductShortResponse;
import com.marketplace.serviceProduct.exception.HttpServletRequestException;
import com.marketplace.serviceProduct.service.ProductService;
import com.marketplace.serviceProduct.service.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ProductController productController;

    @Test
    void addProduct_WithValidRequest_ShouldReturnCreated() {
        Long sellerId = 1L;
        String sellerName = "Test Seller";
        List<AddProductRequest> requests = List.of(mock(AddProductRequest.class));
        List<ProductDetailsResponse> responses = List.of(mock(ProductDetailsResponse.class));

        when(jwtService.extractSellerId(request)).thenReturn(sellerId);
        when(jwtService.extractSellerName(request)).thenReturn(sellerName);
        when(productService.addProducts(sellerId, sellerName, requests)).thenReturn(responses);

        ResponseEntity<?> response = productController.addProduct(request, requests);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responses, response.getBody());
        verify(jwtService).extractSellerId(request);
        verify(jwtService).extractSellerName(request);
        verify(productService).addProducts(sellerId, sellerName, requests);
    }

    @Test
    void addProduct_WithInvalidToken_ShouldReturnUnauthorized() {
        List<AddProductRequest> requests = List.of(new AddProductRequest());
        String errorMessage = "Invalid token";

        when(jwtService.extractSellerId(request)).thenThrow(new HttpServletRequestException(errorMessage));

        ResponseEntity<?> response = productController.addProduct(request, requests);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(jwtService).extractSellerId(request);
        verifyNoInteractions(productService);
    }

    @Test
    void addProduct_WithServiceException_ShouldReturnBadRequest() {
        Long sellerId = 1L;
        String sellerName = "Test Seller";
        List<AddProductRequest> requests = List.of(new AddProductRequest());
        String errorMessage = "Invalid product data";

        when(jwtService.extractSellerId(request)).thenReturn(sellerId);
        when(jwtService.extractSellerName(request)).thenReturn(sellerName);
        when(productService.addProducts(sellerId, sellerName, requests))
                .thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = productController.addProduct(request, requests);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(jwtService).extractSellerId(request);
        verify(jwtService).extractSellerName(request);
        verify(productService).addProducts(sellerId, sellerName, requests);
    }

    @Test
    void getProducts_WithValidPageable_ShouldReturnProducts() {
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductShortResponse> expectedPage = Page.empty();

        when(productService.getProductShort(pageable)).thenReturn(expectedPage);

        ResponseEntity<Page<ProductShortResponse>> response = productController.getProducts(page, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPage, response.getBody());
        verify(productService).getProductShort(pageable);
    }

    @Test
    void getProducts_WithCustomPageable_ShouldReturnProducts() {
        int page = 2;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductShortResponse> expectedPage = Page.empty();

        when(productService.getProductShort(pageable)).thenReturn(expectedPage);

        ResponseEntity<Page<ProductShortResponse>> response = productController.getProducts(page, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPage, response.getBody());
        verify(productService).getProductShort(pageable);
    }

    @Test
    void getProductDetail_WithValidProductId_ShouldReturnProduct() {
        Long productId = 1L;
        ProductDetailsResponse expectedResponse = mock(ProductDetailsResponse.class);

        when(productService.getProductDetail(productId)).thenReturn(expectedResponse);

        ResponseEntity<?> response = productController.getProductDetail(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(productService).getProductDetail(productId);
    }

    @Test
    void getProductDetail_WithInvalidProductId_ShouldReturnBadRequest() {
        Long productId = 999L;
        String errorMessage = "Product not found";

        when(productService.getProductDetail(productId))
                .thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = productController.getProductDetail(productId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(productService).getProductDetail(productId);
    }

}
