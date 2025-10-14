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
    void validateProduct_WithExistingProduct_ShouldReturnTrue() {
        Long productId = 1L;
        Product.ValidateProductRequest request = Product.ValidateProductRequest.newBuilder()
                .setProductId(productId)
                .build();

        productGrpcService.validateProduct(request, responseObserver);

        verify(productService).findProductById(productId);
        verify(responseObserver).onNext(argThat(response ->
                response.getProductExist() == true
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
                .when(productService).findProductById(productId);

        productGrpcService.validateProduct(request, responseObserver);

        verify(productService).findProductById(productId);
        verify(responseObserver).onNext(argThat(response ->
                response.getProductExist() == false
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

        productGrpcService.validateProduct(request, responseObserver);

        InOrder inOrder = inOrder(responseObserver);
        inOrder.verify(responseObserver).onNext(any(Product.ValidateProductResponse.class));
        inOrder.verify(responseObserver).onCompleted();
    }

}
