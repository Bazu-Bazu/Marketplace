package com.marketplace.serviceauth.controller;

import com.marketplace.serviceauth.dto.request.UpdateUserRequest;
import com.marketplace.serviceauth.dto.response.UserResponse;
import com.marketplace.serviceauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/update")
    public ResponseEntity<?> updateUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateUserRequest request)
    {
        try {
            String email = userDetails.getUsername();

            UserResponse response = userService.updateUser(email, request);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();

            UserResponse response = userService.getUser(email);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();

            userService.deleteUser(email);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

}
