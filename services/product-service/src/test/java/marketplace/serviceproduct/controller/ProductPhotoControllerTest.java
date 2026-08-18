package marketplace.serviceproduct.controller;

import com.marketplace.serviceProduct.controller.ProductPhotoController;
import com.marketplace.serviceProduct.dto.response.ProductPhotoResponse;
import com.marketplace.serviceProduct.service.ProductPhotoService;
import com.marketplace.serviceProduct.service.ProductService;
import com.marketplace.serviceProduct.service.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductPhotoControllerTest {

    @Mock
    private ProductPhotoService productPhotoService;

    @Mock
    private ProductService productService;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MultipartFile file1;

    @Mock
    private MultipartFile file2;

    @InjectMocks
    private ProductPhotoController productPhotoController;

    @Test
    void addProductPhotos_WithValidRequest_ShouldReturnCreated() {
        Long sellerId = 1L;
        Long productId = 100L;
        List<MultipartFile> files = List.of(file1, file2);
        List<ProductPhotoResponse> responses = List.of(mock(ProductPhotoResponse.class));

        when(jwtService.extractSellerId(request)).thenReturn(sellerId);
        when(productPhotoService.addProductPhotos(files, productId)).thenReturn(responses);

        ResponseEntity<?> result = productPhotoController.addProductPhotos(request, files, productId);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(responses, result.getBody());
        verify(jwtService).extractSellerId(request);
        verify(productService).validateProductOwnership(sellerId, productId);
        verify(productPhotoService).addProductPhotos(files, productId);
    }

    @Test
    void addProductPhotos_WithAccessDenied_ShouldReturnForbidden() {
        Long sellerId = 1L;
        Long productId = 100L;
        List<MultipartFile> files = List.of(file1);
        String errorMessage = "Product doesn't belong to current seller";

        when(jwtService.extractSellerId(request)).thenReturn(sellerId);
        doThrow(new AccessDeniedException(errorMessage))
                .when(productService).validateProductOwnership(sellerId, productId);

        ResponseEntity<?> result = productPhotoController.addProductPhotos(request, files, productId);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertEquals(errorMessage, result.getBody());
        verify(jwtService).extractSellerId(request);
        verify(productService).validateProductOwnership(sellerId, productId);
        verifyNoInteractions(productPhotoService);
    }

    @Test
    void addProductPhotos_WithServiceException_ShouldReturnBadRequest() {
        Long sellerId = 1L;
        Long productId = 100L;
        List<MultipartFile> files = List.of(file1);
        String errorMessage = "Failed to process photos";

        when(jwtService.extractSellerId(request)).thenReturn(sellerId);
        when(productPhotoService.addProductPhotos(files, productId))
                .thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> result = productPhotoController.addProductPhotos(request, files, productId);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(errorMessage, result.getBody());
        verify(jwtService).extractSellerId(request);
        verify(productService).validateProductOwnership(sellerId, productId);
        verify(productPhotoService).addProductPhotos(files, productId);
    }

    @Test
    void downloadFile_WithValidFileId_ShouldReturnFile() {
        String fileId = "507f1f77bcf86cd799439011";
        ResponseEntity<org.springframework.core.io.Resource> expectedResponse = ResponseEntity.ok().build();

        when(productPhotoService.downloadPhoto(fileId)).thenReturn(expectedResponse);

        ResponseEntity<?> result = productPhotoController.downloadFile(fileId);

        assertEquals(expectedResponse, result);
        verify(productPhotoService).downloadPhoto(fileId);
    }

    @Test
    void downloadFile_WithInvalidFileId_ShouldReturnBadRequest() {
        String fileId = "invalidFileId";
        String errorMessage = "File not found";

        when(productPhotoService.downloadPhoto(fileId))
                .thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> result = productPhotoController.downloadFile(fileId);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(errorMessage, result.getBody());
        verify(productPhotoService).downloadPhoto(fileId);
    }

}