package com.example.Marketplace.controller;

import com.example.Marketplace.dto.UserDto;
import com.example.Marketplace.model.User;
import com.example.Marketplace.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/app/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/start-registration")
    public ResponseEntity<?> startRegistration(@RequestParam String email, @RequestParam String password) {
        authService.startRegistration(email, password);

        return ResponseEntity.ok("A code was sent to the mail");
    }

    @PostMapping("/finish-registration")
    public ResponseEntity<User> finishRegistration(@RequestBody @Valid UserDto userDto, @RequestParam String code) {
        User user = authService.finishRegistration(userDto, code);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/start-login")
    public ResponseEntity<?> startLogin(@RequestParam String email, @RequestParam String password) {
        authService.startLogin(email, password);

        return ResponseEntity.ok("A code was sent to the mail");
    }

    @PostMapping("/finish-login")
    public ResponseEntity<?> finishLogin(@RequestParam String email, @RequestParam String code) {
        Authentication authentication = authService.finishLogin(email, code);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseEntity.ok("Login successful");
    }

}
