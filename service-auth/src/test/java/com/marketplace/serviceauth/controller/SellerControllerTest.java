package com.marketplace.serviceauth.controller;

import com.marketplace.serviceauth.dto.request.RegisterSellerRequest;
import com.marketplace.serviceauth.dto.request.UpdateSellerRequest;
import com.marketplace.serviceauth.dto.response.SellerResponse;
import com.marketplace.serviceauth.service.SellerService;
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
class SellerControllerTest {

    @Mock
    private SellerService sellerService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private SellerController sellerController;

    private RegisterSellerRequest registerRequest;
    private UpdateSellerRequest updateRequest;
    private SellerResponse sellerResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterSellerRequest();
        registerRequest.setName("Test Seller");

        updateRequest = new UpdateSellerRequest();
        updateRequest.setName("Updated Seller");
        updateRequest.setEmail("updated@example.com");

        sellerResponse = SellerResponse.builder()
                .id(1L)
                .name("Test Seller")
                .email("test@example.com")
                .build();
    }

    @Test
    void registerSeller_WithValidRequest_ShouldReturnSellerResponse() {
        String email = "test@example.com";
        when(userDetails.getUsername()).thenReturn(email);
        when(sellerService.registerSeller(email, registerRequest)).thenReturn(sellerResponse);

        ResponseEntity<?> response = sellerController.registerSeller(userDetails, registerRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sellerResponse, response.getBody());
        verify(sellerService).registerSeller(email, registerRequest);
    }

    @Test
    void registerSeller_WithException_ShouldReturnBadRequest() {
        String email = "test@example.com";
        String errorMessage = "Seller already exists";
        when(userDetails.getUsername()).thenReturn(email);
        when(sellerService.registerSeller(email, registerRequest))
                .thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = sellerController.registerSeller(userDetails, registerRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(sellerService).registerSeller(email, registerRequest);
    }

    @Test
    void registerSeller_WithNullUserDetails_ShouldReturnBadRequest() {
        ResponseEntity<?> response = sellerController.registerSeller(null, registerRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("null"));
    }

    @Test
    void updateSeller_WithValidRequest_ShouldReturnSellerResponse() {
        String email = "test@example.com";
        when(userDetails.getUsername()).thenReturn(email);
        when(sellerService.updateSeller(email, updateRequest)).thenReturn(sellerResponse);

        ResponseEntity<?> response = sellerController.updateSeller(userDetails, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sellerResponse, response.getBody());
        verify(sellerService).updateSeller(email, updateRequest);
    }

    @Test
    void updateSeller_WithException_ShouldReturnBadRequest() {
        String email = "test@example.com";
        String errorMessage = "Seller not found";
        when(userDetails.getUsername()).thenReturn(email);
        when(sellerService.updateSeller(email, updateRequest))
                .thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = sellerController.updateSeller(userDetails, updateRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(sellerService).updateSeller(email, updateRequest);
    }

    @Test
    void updateSeller_WithNullUserDetails_ShouldReturnBadRequest() {
        ResponseEntity<?> response = sellerController.updateSeller(null, updateRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("null"));
    }

    @Test
    void getSeller_WithValidSeller_ShouldReturnSellerResponse() {
        String email = "test@example.com";
        when(userDetails.getUsername()).thenReturn(email);
        when(sellerService.getSeller(email)).thenReturn(sellerResponse);

        ResponseEntity<?> response = sellerController.getSeller(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sellerResponse, response.getBody());
        verify(sellerService).getSeller(email);
    }

    @Test
    void getSeller_WithException_ShouldReturnInternalServerError() {
        String email = "test@example.com";
        String errorMessage = "Database error";
        when(userDetails.getUsername()).thenReturn(email);
        when(sellerService.getSeller(email)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = sellerController.getSeller(userDetails);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(sellerService).getSeller(email);
    }

    @Test
    void getSeller_WithNullUserDetails_ShouldReturnInternalServerError() {
        ResponseEntity<?> response = sellerController.getSeller(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("null"));
    }

    @Test
    void deleteSeller_WithValidSeller_ShouldReturnNoContent() {
        String email = "test@example.com";
        when(userDetails.getUsername()).thenReturn(email);
        doNothing().when(sellerService).deleteSeller(email);

        ResponseEntity<?> response = sellerController.deleteSeller(userDetails);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(sellerService).deleteSeller(email);
    }

    @Test
    void deleteSeller_WithException_ShouldReturnInternalServerError() {
        String email = "test@example.com";
        String errorMessage = "Seller not found";
        when(userDetails.getUsername()).thenReturn(email);
        doThrow(new RuntimeException(errorMessage)).when(sellerService).deleteSeller(email);

        ResponseEntity<?> response = sellerController.deleteSeller(userDetails);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(sellerService).deleteSeller(email);
    }

    @Test
    void deleteSeller_WithNullUserDetails_ShouldReturnInternalServerError() {
        ResponseEntity<?> response = sellerController.deleteSeller(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("null"));
    }

    @Test
    void updateSeller_WithPartialUpdate_ShouldReturnSellerResponse() {
        String email = "test@example.com";
        UpdateSellerRequest partialUpdate = new UpdateSellerRequest();
        partialUpdate.setName("Updated Name Only");

        SellerResponse updatedResponse = SellerResponse.builder()
                .id(1L)
                .name("Updated Name Only")
                .email("test@example.com")
                .build();

        when(userDetails.getUsername()).thenReturn(email);
        when(sellerService.updateSeller(email, partialUpdate)).thenReturn(updatedResponse);

        ResponseEntity<?> response = sellerController.updateSeller(userDetails, partialUpdate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedResponse, response.getBody());
        verify(sellerService).updateSeller(email, partialUpdate);
    }

    @Test
    void getSeller_WithNonExistentSeller_ShouldReturnError() {
        String email = "nonexistent@example.com";
        String errorMessage = "Seller not found";
        when(userDetails.getUsername()).thenReturn(email);
        when(sellerService.getSeller(email)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = sellerController.getSeller(userDetails);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(sellerService).getSeller(email);
    }

    @Test
    void registerSeller_WithEmptyRequest_ShouldReturnBadRequest() {
        String email = "test@example.com";
        RegisterSellerRequest emptyRequest = new RegisterSellerRequest();
        String errorMessage = "Seller name is required";

        when(userDetails.getUsername()).thenReturn(email);
        when(sellerService.registerSeller(email, emptyRequest))
                .thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = sellerController.registerSeller(userDetails, emptyRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(sellerService).registerSeller(email, emptyRequest);
    }

}