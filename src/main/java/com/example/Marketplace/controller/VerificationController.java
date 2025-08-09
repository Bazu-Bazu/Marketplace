package com.example.Marketplace.controller;

import com.example.Marketplace.service.VerificationService;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping("/send-code")
    public ResponseEntity<String> sendVarificationCode(@RequestParam @Email String email) {
        verificationService.sendVerificationCode(email);

        return ResponseEntity.ok("The code has been sent by email");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCode(@RequestParam @Email String email, @RequestParam String code) {
        if (verificationService.verifyCode(email, code)) {
            return ResponseEntity.ok("Email has been successfully confirmed");
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("The code is incorrect or has expired");
    }

}
