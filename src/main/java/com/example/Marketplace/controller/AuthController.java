package com.example.Marketplace.controller;

import com.example.Marketplace.dto.AuthResponse;
import com.example.Marketplace.dto.LoginUserDto;
import com.example.Marketplace.dto.RegisterUserDto;
import com.example.Marketplace.dto.VerifyUserDto;
import com.example.Marketplace.exception.InvalidTokenException;
import com.example.Marketplace.service.AuthService;
import com.example.Marketplace.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterUserDto registerUserDto) {
        AuthResponse response = authService.signUp(registerUserDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        AuthResponse response = authService.authenticate(loginUserDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        AuthResponse response = authService.verifyUser(verifyUserDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-code")
    public ResponseEntity<AuthResponse> resendVerificationCode(@RequestParam String email) {
        AuthResponse response = authService.resendVerificationCode(email);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid authorization header");
        }

        String refreshToken = authHeader.substring(7);
        AuthResponse response = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid authorization header");
        }

        String refreshToken = authHeader.substring(7);
        AuthResponse response = authService.logout(refreshToken);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout-all")
    public ResponseEntity<AuthResponse> logoutAll(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid authorization header");
        }

        String refreshToken = authHeader.substring(7);
        String email = jwtService.extractUsername(refreshToken);
        AuthResponse response = authService.logoutAllSessions(email);

        return ResponseEntity.ok(response);
    }

}
