package marketplace.serviceproduct.service;

import com.marketplace.serviceProduct.dto.request.AddCategoryRequest;
import com.marketplace.serviceProduct.dto.response.CategoryResponse;
import com.marketplace.serviceProduct.entity.Category;
import com.marketplace.serviceProduct.exception.CategoryException;
import com.marketplace.serviceProduct.repository.CategoryRepository;
import com.marketplace.serviceProduct.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private final Long CATEGORY_ID = 1L;
    private final Long PARENT_ID = 2L;
    private final String CATEGORY_NAME = "electronics";
    private final String PARENT_NAME = "technology";

    @Test
    void addCategory_WithValidRequest_ShouldCreateCategory() {
        AddCategoryRequest request = createAddCategoryRequest("Electronics", null);
        Category savedCategory = createCategory(CATEGORY_ID, CATEGORY_NAME, null);

        when(categoryRepository.findByName(CATEGORY_NAME)).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryResponse response = categoryService.addCategory(request);

        assertNotNull(response);
        assertEquals(CATEGORY_NAME, response.getName());
        assertNull(response.getParentId());
        assertNull(response.getParentName());

        verify(categoryRepository).findByName(CATEGORY_NAME);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void addCategory_WithParentCategory_ShouldCreateCategoryWithParent() {
        AddCategoryRequest request = createAddCategoryRequest("Laptops", PARENT_ID);
        Category parentCategory = createCategory(PARENT_ID, PARENT_NAME, null);
        Category savedCategory = createCategory(CATEGORY_ID, "laptops", parentCategory);

        when(categoryRepository.findByName("laptops")).thenReturn(Optional.empty());
        when(categoryRepository.findById(PARENT_ID)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryResponse response = categoryService.addCategory(request);

        assertNotNull(response);
        assertEquals(PARENT_ID, response.getParentId());
        assertEquals(PARENT_NAME, response.getParentName());

        verify(categoryRepository).findById(PARENT_ID);
    }

    @Test
    void addCategory_WhenCategoryAlreadyExists_ShouldThrowException() {
        AddCategoryRequest request = createAddCategoryRequest("Electronics", null);
        Category existingCategory = createCategory(CATEGORY_ID, CATEGORY_NAME, null);

        when(categoryRepository.findByName(CATEGORY_NAME)).thenReturn(Optional.of(existingCategory));

        CategoryException exception = assertThrows(CategoryException.class,
                () -> categoryService.addCategory(request));

        assertEquals("Category already exists.", exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void addCategory_WithNonExistentParent_ShouldThrowException() {
        AddCategoryRequest request = createAddCategoryRequest("Laptops", PARENT_ID);

        when(categoryRepository.findByName("laptops")).thenReturn(Optional.empty());
        when(categoryRepository.findById(PARENT_ID)).thenReturn(Optional.empty());

        CategoryException exception = assertThrows(CategoryException.class,
                () -> categoryService.addCategory(request));

        assertEquals("Category not found.", exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void addCategory_ShouldConvertNameToLowerCase() {
        AddCategoryRequest request = createAddCategoryRequest("ELECTRONICS", null);
        Category savedCategory = createCategory(CATEGORY_ID, CATEGORY_NAME, null);

        when(categoryRepository.findByName(CATEGORY_NAME)).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryResponse response = categoryService.addCategory(request);

        assertEquals(CATEGORY_NAME, response.getName());
        verify(categoryRepository).findByName(CATEGORY_NAME);
    }

    @Test
    void deleteCategory_WithExistingCategory_ShouldDeleteCategory() {
        Category category = createCategory(CATEGORY_ID, CATEGORY_NAME, null);

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(category);

        categoryService.deleteCategory(CATEGORY_ID);

        verify(categoryRepository).findById(CATEGORY_ID);
        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_WithNonExistentCategory_ShouldThrowException() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        CategoryException exception = assertThrows(CategoryException.class,
                () -> categoryService.deleteCategory(CATEGORY_ID));

        assertEquals("Category not found.", exception.getMessage());
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void deleteCategory_WithCategoryHavingChildren_ShouldDeleteSuccessfully() {
        Category parentCategory = createCategory(PARENT_ID, PARENT_NAME, null);
        Category childCategory = createCategory(CATEGORY_ID, CATEGORY_NAME, parentCategory);

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(childCategory));
        doNothing().when(categoryRepository).delete(childCategory);

        assertDoesNotThrow(() -> categoryService.deleteCategory(CATEGORY_ID));
        verify(categoryRepository).delete(childCategory);
    }

    private AddCategoryRequest createAddCategoryRequest(String name, Long parentId) {
        AddCategoryRequest request = new AddCategoryRequest();
        request.setName(name);
        request.setParentId(parentId);

        return request;
    }

    private Category createCategory(Long id, String name, Category parent) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setParent(parent);
        return category;
    }

}