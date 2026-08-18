package marketplace.serviceproduct.service;

import com.marketplace.serviceProduct.dto.request.AddProductRequest;
import com.marketplace.serviceProduct.dto.response.ProductDetailsResponse;
import com.marketplace.serviceProduct.dto.response.ProductShortResponse;
import com.marketplace.serviceProduct.entity.Category;
import com.marketplace.serviceProduct.entity.Product;
import com.marketplace.serviceProduct.exception.CategoryException;
import com.marketplace.serviceProduct.exception.ProductException;
import com.marketplace.serviceProduct.repository.CategoryRepository;
import com.marketplace.serviceProduct.repository.ProductRepository;
import com.marketplace.serviceProduct.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private final Long SELLER_ID = 1L;
    private final String SELLER_NAME = "Test Seller";
    private final Long PRODUCT_ID = 100L;
    private final Long CATEGORY_ID_1 = 10L;
    private final Long CATEGORY_ID_2 = 20L;

    @Test
    void addProducts_WithValidRequest_ShouldCreateProducts() {
        AddProductRequest request = createAddProductRequest();
        Set<Long> categoryIds = Set.of(CATEGORY_ID_1, CATEGORY_ID_2);
        Set<Category> categories = createCategories();
        Product savedProduct = createProduct();

        when(categoryRepository.findAllByIdIn(categoryIds)).thenReturn(categories);
        when(productRepository.saveAll(anyList())).thenReturn(List.of(savedProduct));

        List<ProductDetailsResponse> responses = productService.addProducts(
                SELLER_ID, SELLER_NAME, List.of(request)
        );

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(PRODUCT_ID, responses.get(0).getId());
        verify(categoryRepository).findAllByIdIn(categoryIds);
        verify(productRepository).saveAll(anyList());
    }

    @Test
    void addProducts_WithNonExistentCategories_ShouldThrowException() {
        AddProductRequest request = createAddProductRequest();
        Set<Long> requestedIds = Set.of(CATEGORY_ID_1, CATEGORY_ID_2);

        when(categoryRepository.findAllByIdIn(requestedIds)).thenReturn(Set.of());

        CategoryException exception = assertThrows(CategoryException.class,
                () -> productService.addProducts(SELLER_ID, SELLER_NAME, List.of(request)));

        assertTrue(exception.getMessage().contains("Categories not found"));
    }

    @Test
    void findProductById_WithExistingProduct_ShouldReturnProduct() {
        Product product = createProduct();
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        Product result = productService.findProductById(PRODUCT_ID);

        assertNotNull(result);
        assertEquals(PRODUCT_ID, result.getId());
    }

    @Test
    void findProductById_WithNonExistentProduct_ShouldThrowException() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        ProductException exception = assertThrows(ProductException.class,
                () -> productService.findProductById(PRODUCT_ID));

        assertEquals("Product not found.", exception.getMessage());
    }

    @Test
    void getProductShort_ShouldReturnPageOfProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = createProduct();
        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        Page<ProductShortResponse> result = productService.getProductShort(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(PRODUCT_ID, result.getContent().get(0).getId());
    }

    @Test
    void getProductDetail_WithExistingProduct_ShouldReturnDetails() {
        Product product = createProduct();
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        ProductDetailsResponse response = productService.getProductDetail(PRODUCT_ID);

        assertNotNull(response);
        assertEquals(PRODUCT_ID, response.getId());
        assertEquals(2, response.getCategoryIds().size());
    }

    @Test
    void validateProductOwnership_WithValidOwner_ShouldNotThrowException() {
        Product product = createProduct();
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        assertDoesNotThrow(() -> productService.validateProductOwnership(SELLER_ID, PRODUCT_ID));
    }

    @Test
    void validateProductOwnership_WithInvalidOwner_ShouldThrowAccessDeniedException() {
        Product product = createProduct();
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> productService.validateProductOwnership(999L, PRODUCT_ID));

        assertTrue(exception.getMessage().contains("Product doesn't belong to current seller"));
    }

    @Test
    void addPhotos_WithValidProduct_ShouldAddPhotos() {
        Product product = createProduct();
        List<String> newPhotos = List.of("photo3.jpg", "photo4.jpg");

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productService.addPhotos(PRODUCT_ID, newPhotos);

        assertTrue(product.getUrls().containsAll(newPhotos));
        verify(productRepository).save(product);
    }

    private AddProductRequest createAddProductRequest() {
        AddProductRequest request = new AddProductRequest();
        request.setName("Test Product");
        request.setPrice(1000);
        request.setCount(10);
        request.setDescription("Test Description");
        request.setCategoryIds(List.of(CATEGORY_ID_1, CATEGORY_ID_2));

        return request;
    }

    private Set<Category> createCategories() {
        Category category1 = new Category();
        category1.setId(CATEGORY_ID_1);
        category1.setName("Category 1");

        Category category2 = new Category();
        category2.setId(CATEGORY_ID_2);
        category2.setName("Category 2");

        return Set.of(category1, category2);
    }

    private Product createProduct() {
        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setName("Test Product");
        product.setPrice(1000);
        product.setCount(10);
        product.setDescription("Test Description");
        product.setSellerId(SELLER_ID);
        product.setSellerName(SELLER_NAME);
        product.setUrls(new ArrayList<>(List.of("photo1.jpg", "photo2.jpg")));
        product.setCategories(new HashSet<>(createCategories()));
        return product;
    }

}
