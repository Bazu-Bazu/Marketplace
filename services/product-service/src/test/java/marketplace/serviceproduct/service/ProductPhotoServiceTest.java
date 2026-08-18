package marketplace.serviceproduct.service;

import com.marketplace.serviceProduct.dto.response.ProductPhotoResponse;
import com.marketplace.serviceProduct.entity.Product;
import com.marketplace.serviceProduct.entity.ProductPhoto;
import com.marketplace.serviceProduct.exception.ProductPhotoException;
import com.marketplace.serviceProduct.repository.ProductPhotoRepository;
import com.marketplace.serviceProduct.service.ProductPhotoService;
import com.marketplace.serviceProduct.service.ProductService;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.core.io.Resource;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductPhotoServiceTest {

    @Mock
    private ProductPhotoRepository productPhotoRepository;

    @Mock
    private ProductService productService;

    @Mock
    private GridFsTemplate gridFsTemplate;

    @InjectMocks
    private ProductPhotoService productPhotoService;

    private final Long PRODUCT_ID = 1L;
    private final String FILE_ID = "507f1f77bcf86cd799439011";

    @Test
    void addProductPhotos_WithValidPhotos_ShouldCreatePhotos() throws IOException {
        MultipartFile photo1 = mock(MultipartFile.class);
        when(photo1.getOriginalFilename()).thenReturn("photo1.jpg");
        when(photo1.getContentType()).thenReturn("image/jpeg");
        when(photo1.getSize()).thenReturn(1024L);
        when(photo1.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));

        MultipartFile photo2 = mock(MultipartFile.class);
        when(photo2.getOriginalFilename()).thenReturn("photo2.jpg");
        when(photo2.getContentType()).thenReturn("image/jpeg");
        when(photo2.getSize()).thenReturn(1024L);
        when(photo2.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));

        List<MultipartFile> photos = List.of(photo1, photo2);

        ProductPhoto savedPhoto1 = createProductPhoto("photo1.jpg", "image/jpeg");
        ProductPhoto savedPhoto2 = createProductPhoto("photo2.jpg", "image/jpeg");

        when(productService.findProductById(PRODUCT_ID)).thenReturn(new Product());
        when(gridFsTemplate.store(any(InputStream.class), anyString(), anyString()))
                .thenReturn(new ObjectId(FILE_ID));
        when(productPhotoRepository.saveAll(anyList())).thenReturn(List.of(savedPhoto1, savedPhoto2));

        List<ProductPhotoResponse> responses = productPhotoService.addProductPhotos(photos, PRODUCT_ID);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(productService).findProductById(PRODUCT_ID);
        verify(productPhotoRepository).saveAll(anyList());
        verify(productService).addPhotos(eq(PRODUCT_ID), anyList());
    }

    @Test
    void addProductPhotos_WithIOException_ShouldThrowException() throws IOException {
        MultipartFile photo = mock(MultipartFile.class);
        when(photo.getInputStream()).thenThrow(new IOException());

        when(productService.findProductById(PRODUCT_ID)).thenReturn(new Product());

        ProductPhotoException exception = assertThrows(ProductPhotoException.class,
                () -> productPhotoService.addProductPhotos(List.of(photo), PRODUCT_ID));

        assertEquals("Creating ProductPhoto error.", exception.getMessage());
    }

    @Test
    void downloadPhoto_WithValidFileId_ShouldReturnResource() {
        GridFSFile gridFSFile = mock(GridFSFile.class);
        GridFsResource resource = mock(GridFsResource.class);

        when(gridFsTemplate.findOne(any(Query.class))).thenReturn(gridFSFile);
        when(gridFsTemplate.getResource(gridFSFile)).thenReturn(resource);
        when(resource.getContentType()).thenReturn("image/jpeg");
        when(resource.getFilename()).thenReturn("photo.jpg");

        ResponseEntity<Resource> response = productPhotoService.downloadPhoto(FILE_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        verify(gridFsTemplate).findOne(any(Query.class));
        verify(gridFsTemplate).getResource(gridFSFile);
    }

    @Test
    void downloadPhoto_WithInvalidFileId_ShouldThrowException() {
        when(gridFsTemplate.findOne(any(Query.class))).thenReturn(null);

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> productPhotoService.downloadPhoto(FILE_ID));

        assertNotNull(exception);
    }

    private ProductPhoto createProductPhoto(String filename, String contentType) {
        ProductPhoto photo = new ProductPhoto();
        photo.setId(FILE_ID);
        photo.setFileName(filename);
        photo.setContentType(contentType);
        photo.setSize(1024L);
        photo.setProductId(PRODUCT_ID);
        photo.setUrl("/product-photo/" + FILE_ID);
        return photo;
    }

}
