package marketplace.serviceproduct.controller;

import com.marketplace.serviceProduct.controller.CategoryController;
import com.marketplace.serviceProduct.dto.request.AddCategoryRequest;
import com.marketplace.serviceProduct.dto.response.CategoryResponse;
import com.marketplace.serviceProduct.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    void addCategory_WithValidRequest_ShouldReturnOk() {
        AddCategoryRequest request = mock(AddCategoryRequest.class);
        CategoryResponse response = mock(CategoryResponse.class);

        when(categoryService.addCategory(request)).thenReturn(response);

        ResponseEntity<?> result = categoryController.addCategory(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(categoryService).addCategory(request);
    }

    @Test
    void addCategory_WithException_ShouldReturnBadRequest() {
        AddCategoryRequest request = mock(AddCategoryRequest.class);
        String errorMessage = "Category already exists";

        when(categoryService.addCategory(request)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> result = categoryController.addCategory(request);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(errorMessage, result.getBody());
        verify(categoryService).addCategory(request);
    }

    @Test
    void deleteCategory_WithValidId_ShouldReturnNoContent() {
        Long categoryId = 1L;

        ResponseEntity<?> result = categoryController.deleteCategory(categoryId);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(categoryService).deleteCategory(categoryId);
    }

    @Test
    void deleteCategory_WithException_ShouldReturnBadRequest() {
        Long categoryId = 999L;
        String errorMessage = "Category not found";

        doThrow(new RuntimeException(errorMessage)).when(categoryService).deleteCategory(categoryId);

        ResponseEntity<?> result = categoryController.deleteCategory(categoryId);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(errorMessage, result.getBody());
        verify(categoryService).deleteCategory(categoryId);
    }

}
