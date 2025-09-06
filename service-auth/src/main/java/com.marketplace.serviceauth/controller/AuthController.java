package com.marketplace.serviceauth.controller;

import com.marketplace.serviceauth.dto.request.LoginUserRequest;
import com.marketplace.serviceauth.dto.request.RegisterUserRequest;
import com.marketplace.serviceauth.dto.request.VerifyUserRequest;
import com.marketplace.serviceauth.dto.response.AuthResponse;
import com.marketplace.serviceauth.exception.InvalidTokenException;
import com.marketplace.serviceauth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterUserRequest request) {
        AuthResponse response = authService.signUp(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody LoginUserRequest request) {
        AuthResponse response = authService.authenticate(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verifyUser(@RequestBody VerifyUserRequest request) {
        AuthResponse response = authService.verifyUser(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-code")
    public ResponseEntity<AuthResponse> resendVerificationCode(@RequestParam String email) {
        AuthResponse response = authService.resendVerificationCode(email);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid authorization header.");
        }

        String refreshToken = authHeader.substring(7);
        AuthResponse response = authService.updateTokens(refreshToken);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid authorization header");
        }

        String accessToken = authHeader.substring(7);
        AuthResponse response = authService.logout(accessToken);

        return ResponseEntity.ok(response);
    }

}
