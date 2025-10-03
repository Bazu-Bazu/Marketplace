package com.marketplace.serviceauth.controller;

import com.marketplace.serviceauth.dto.request.LoginUserRequest;
import com.marketplace.serviceauth.dto.request.RefreshTokenRequest;
import com.marketplace.serviceauth.dto.request.RegisterUserRequest;
import com.marketplace.serviceauth.dto.request.VerifyUserRequest;
import com.marketplace.serviceauth.dto.response.AuthResponse;
import com.marketplace.serviceauth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthController authController;

    private RegisterUserRequest registerRequest;
    private LoginUserRequest loginRequest;
    private VerifyUserRequest verifyRequest;
    private RefreshTokenRequest refreshRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterUserRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");

        loginRequest = new LoginUserRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        verifyRequest = new VerifyUserRequest();
        verifyRequest.setVerificationCode("123456");
        verifyRequest.setEmail("test@example.com");

        refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken("refreshToken");

        authResponse = AuthResponse.builder()
                .userId(1L)
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
    }

    @Test
    void registerUser_WithValidRequest_ShouldReturnCreated() {
        doNothing().when(authService).signUp(registerRequest);

        ResponseEntity<?> response = authController.registerUser(registerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(authService).signUp(registerRequest);
    }

    @Test
    void registerUser_WithException_ShouldReturnBadRequest() {
        String errorMessage = "User already exists";
        doThrow(new RuntimeException(errorMessage)).when(authService).signUp(registerRequest);

        ResponseEntity<?> response = authController.registerUser(registerRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(authService).signUp(registerRequest);
    }

    @Test
    void authenticate_WithValidRequest_ShouldReturnAuthResponse() {
        when(httpRequest.getHeader("User-Agent")).thenReturn("Chrome");
        when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.1");
        when(authService.authenticate(loginRequest, "Chrome", "192.168.1.1")).thenReturn(authResponse);

        ResponseEntity<?> response = authController.authenticate(loginRequest, httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
        verify(authService).authenticate(loginRequest, "Chrome", "192.168.1.1");
    }

    @Test
    void authenticate_WithException_ShouldReturnBadRequest() {
        String errorMessage = "Invalid credentials";
        when(httpRequest.getHeader("User-Agent")).thenReturn("Chrome");
        when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.1");
        when(authService.authenticate(loginRequest, "Chrome", "192.168.1.1"))
                .thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = authController.authenticate(loginRequest, httpRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(authService).authenticate(loginRequest, "Chrome", "192.168.1.1");
    }

    @Test
    void authenticate_WithNullHeaders_ShouldHandleGracefully() {
        when(httpRequest.getHeader("User-Agent")).thenReturn(null);
        when(httpRequest.getRemoteAddr()).thenReturn(null);
        when(authService.authenticate(loginRequest, null, null)).thenReturn(authResponse);

        ResponseEntity<?> response = authController.authenticate(loginRequest, httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
        verify(authService).authenticate(loginRequest, null, null);
    }

    @Test
    void verifyUser_WithValidRequest_ShouldReturnAuthResponse() {
        when(authService.verifyUser(verifyRequest)).thenReturn(authResponse);

        ResponseEntity<?> response = authController.verifyUser(verifyRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
        verify(authService).verifyUser(verifyRequest);
    }

    @Test
    void verifyUser_WithException_ShouldReturnBadRequest() {
        String errorMessage = "Invalid verification code";
        when(authService.verifyUser(verifyRequest)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = authController.verifyUser(verifyRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(authService).verifyUser(verifyRequest);
    }

    @Test
    void resendVerificationCode_WithValidEmail_ShouldReturnAuthResponse() {
        String email = "test@example.com";
        when(authService.resendVerificationCode(email)).thenReturn(authResponse);

        ResponseEntity<?> response = authController.resendVerificationCode(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
        verify(authService).resendVerificationCode(email);
    }

    @Test
    void resendVerificationCode_WithException_ShouldReturnBadRequest() {
        String email = "test@example.com";
        String errorMessage = "User not found";
        when(authService.resendVerificationCode(email)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = authController.resendVerificationCode(email);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(authService).resendVerificationCode(email);
    }

    @Test
    void refreshToken_WithValidRequest_ShouldReturnAuthResponse() {
        when(httpRequest.getHeader("User-Agent")).thenReturn("Chrome");
        when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.1");
        when(authService.refresh("refreshToken", "Chrome", "192.168.1.1")).thenReturn(authResponse);

        ResponseEntity<?> response = authController.refreshToken(refreshRequest, httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
        verify(authService).refresh("refreshToken", "Chrome", "192.168.1.1");
    }

    @Test
    void refreshToken_WithException_ShouldReturnBadRequest() {
        String errorMessage = "Invalid refresh token";
        when(httpRequest.getHeader("User-Agent")).thenReturn("Chrome");
        when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.1");
        when(authService.refresh("refreshToken", "Chrome", "192.168.1.1"))
                .thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = authController.refreshToken(refreshRequest, httpRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(authService).refresh("refreshToken", "Chrome", "192.168.1.1");
    }

    @Test
    void logout_WithValidRequest_ShouldReturnNoContent() {
        doNothing().when(authService).logout(refreshRequest);

        ResponseEntity<?> response = authController.logout(refreshRequest);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(authService).logout(refreshRequest);
    }

    @Test
    void logout_WithException_ShouldReturnBadRequest() {
        String errorMessage = "Token not found";
        doThrow(new RuntimeException(errorMessage)).when(authService).logout(refreshRequest);

        ResponseEntity<?> response = authController.logout(refreshRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(authService).logout(refreshRequest);
    }

    @Test
    void logoutAll_WithValidUser_ShouldReturnNoContent() {
        when(userDetails.getUsername()).thenReturn("test@example.com");
        doNothing().when(authService).logoutAll("test@example.com");

        ResponseEntity<?> response = authController.logoutAll(userDetails);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(authService).logoutAll("test@example.com");
    }

    @Test
    void logoutAll_WithException_ShouldReturnInternalServerError() {
        String errorMessage = "Database error";
        when(userDetails.getUsername()).thenReturn("test@example.com");
        doThrow(new RuntimeException(errorMessage)).when(authService).logoutAll("test@example.com");

        ResponseEntity<?> response = authController.logoutAll(userDetails);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(authService).logoutAll("test@example.com");
    }

    @Test
    void logoutAll_WithNullUserDetails_ShouldHandleGracefully() {
        ResponseEntity<?> response = authController.logoutAll(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("null"));
    }

}