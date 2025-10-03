package com.marketplace.serviceauth.controller;

import com.marketplace.serviceauth.dto.request.UpdateUserRequest;
import com.marketplace.serviceauth.dto.response.UserResponse;
import com.marketplace.serviceauth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UserController userController;

    private UpdateUserRequest updateRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        updateRequest = new UpdateUserRequest();
        updateRequest.setFirstName("John");
        updateRequest.setLastName("Doe");
        updateRequest.setEmail("john.doe@example.com");

        userResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
    }

    @Test
    void updateUser_WithValidRequest_ShouldReturnUserResponse() {
        String email = "test@example.com";
        when(userDetails.getUsername()).thenReturn(email);
        when(userService.updateUser(email, updateRequest)).thenReturn(userResponse);

        ResponseEntity<?> response = userController.updateUser(userDetails, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verify(userService).updateUser(email, updateRequest);
    }

    @Test
    void updateUser_WithException_ShouldReturnBadRequest() {
        String email = "test@example.com";
        String errorMessage = "User not found";
        when(userDetails.getUsername()).thenReturn(email);
        when(userService.updateUser(email, updateRequest)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = userController.updateUser(userDetails, updateRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(userService).updateUser(email, updateRequest);
    }

    @Test
    void updateUser_WithNullUserDetails_ShouldReturnBadRequest() {
        ResponseEntity<?> response = userController.updateUser(null, updateRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("null"));
    }

    @Test
    void getUser_WithValidUser_ShouldReturnUserResponse() {
        String email = "test@example.com";
        when(userDetails.getUsername()).thenReturn(email);
        when(userService.getUser(email)).thenReturn(userResponse);

        ResponseEntity<?> response = userController.getUser(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verify(userService).getUser(email);
    }

    @Test
    void getUser_WithException_ShouldReturnInternalServerError() {
        String email = "test@example.com";
        String errorMessage = "Database error";
        when(userDetails.getUsername()).thenReturn(email);
        when(userService.getUser(email)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = userController.getUser(userDetails);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(userService).getUser(email);
    }

    @Test
    void getUser_WithNullUserDetails_ShouldReturnInternalServerError() {
        ResponseEntity<?> response = userController.getUser(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("null"));
    }

    @Test
    void deleteUser_WithValidUser_ShouldReturnNoContent() {
        String email = "test@example.com";
        when(userDetails.getUsername()).thenReturn(email);
        doNothing().when(userService).deleteUser(email);

        ResponseEntity<?> response = userController.deleteUser(userDetails);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).deleteUser(email);
    }

    @Test
    void deleteUser_WithException_ShouldReturnInternalServerError() {
        String email = "test@example.com";
        String errorMessage = "User not found";
        when(userDetails.getUsername()).thenReturn(email);
        doThrow(new RuntimeException(errorMessage)).when(userService).deleteUser(email);

        ResponseEntity<?> response = userController.deleteUser(userDetails);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(userService).deleteUser(email);
    }

    @Test
    void deleteUser_WithNullUserDetails_ShouldReturnInternalServerError() {
        ResponseEntity<?> response = userController.deleteUser(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("null"));
    }

    @Test
    void updateUser_WithPartialUpdate_ShouldReturnUserResponse() {
        String email = "test@example.com";
        UpdateUserRequest partialUpdate = new UpdateUserRequest();
        partialUpdate.setFirstName("UpdatedName");

        UserResponse updatedResponse = UserResponse.builder()
                .id(1L)
                .firstName("UpdatedName")
                .lastName("Doe")
                .email("test@example.com")
                .build();

        when(userDetails.getUsername()).thenReturn(email);
        when(userService.updateUser(email, partialUpdate)).thenReturn(updatedResponse);

        ResponseEntity<?> response = userController.updateUser(userDetails, partialUpdate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedResponse, response.getBody());
        verify(userService).updateUser(email, partialUpdate);
    }

    @Test
    void getUser_WithNonExistentUser_ShouldReturnError() {
        String email = "nonexistent@example.com";
        String errorMessage = "User not found";
        when(userDetails.getUsername()).thenReturn(email);
        when(userService.getUser(email)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = userController.getUser(userDetails);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(userService).getUser(email);
    }

}