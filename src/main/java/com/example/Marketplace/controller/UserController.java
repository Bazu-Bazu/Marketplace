package com.example.Marketplace.controller;

import com.example.Marketplace.dto.UserDto;
import com.example.Marketplace.model.User;
import com.example.Marketplace.service.UserService;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/start-user-registration")
    public ResponseEntity<?> startUserRegistration(@Email String email) {
        userService.startUserRegistration(email);

        return ResponseEntity.ok("The confirmation code has been sent");
    }

    @PostMapping("/complete-user-registration")
    public ResponseEntity<User> completeUserRegistration(
            @RequestBody UserDto userDto,
            @RequestParam @Email String email,
            @RequestParam String code
    ) {
        User user = userService.completeUserRegistration(userDto, email, code);

        return ResponseEntity.ok(user);
    }

}
