package marketplace.serviceproduct.service.grpc;

import com.marketplace.grpc.Product;
import com.marketplace.serviceProduct.exception.ProductException;
import com.marketplace.serviceProduct.service.ProductService;
import com.marketplace.serviceProduct.service.grpc.ProductGrpcService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductGrpcServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private StreamObserver<Product.ValidateProductResponse> responseObserver;

    private ProductGrpcService productGrpcService;

    @BeforeEach
    void setUp() {
        productGrpcService = new ProductGrpcService(productService);
    }

    @Test
    void validateProduct_WithExistingProduct_ShouldReturnTrueAndPriceAndCount() {
        Long productId = 1L;
        Product.ValidateProductRequest request = Product.ValidateProductRequest.newBuilder()
                .setProductId(productId)
                .build();

        Map<String, Integer> validationResult = new HashMap<>();
        validationResult.put("price", 100);
        validationResult.put("count", 5);

        when(productService.validateProduct(productId)).thenReturn(validationResult);

        productGrpcService.validateProduct(request, responseObserver);

        verify(productService).validateProduct(productId);
        verify(responseObserver).onNext(argThat(response ->
                response.getProductExist() == true &&
                        response.getPrice() == 100 &&
                        response.getCount() == 5
        ));
        verify(responseObserver).onCompleted();
        verifyNoMoreInteractions(responseObserver);
    }

    @Test
    void validateProduct_WithNonExistingProduct_ShouldReturnFalse() {
        Long productId = 999L;
        Product.ValidateProductRequest request = Product.ValidateProductRequest.newBuilder()
                .setProductId(productId)
                .build();

        doThrow(new ProductException("Product not found"))
                .when(productService).validateProduct(productId);

        productGrpcService.validateProduct(request, responseObserver);

        verify(productService).validateProduct(productId);
        verify(responseObserver).onNext(argThat(response ->
                response.getProductExist() == false &&
                        response.getPrice() == 0 &&
                        response.getCount() == 0
        ));
        verify(responseObserver).onCompleted();
        verifyNoMoreInteractions(responseObserver);
    }

    @Test
    void validateProduct_ShouldCallOnNextAndOnCompleted() {
        Long productId = 1L;
        Product.ValidateProductRequest request = Product.ValidateProductRequest.newBuilder()
                .setProductId(productId)
                .build();

        Map<String, Integer> validationResult = new HashMap<>();
        validationResult.put("price", 150);
        validationResult.put("count", 10);

        when(productService.validateProduct(productId)).thenReturn(validationResult);

        productGrpcService.validateProduct(request, responseObserver);

        InOrder inOrder = inOrder(responseObserver);
        inOrder.verify(responseObserver).onNext(any(Product.ValidateProductResponse.class));
        inOrder.verify(responseObserver).onCompleted();
    }

}
