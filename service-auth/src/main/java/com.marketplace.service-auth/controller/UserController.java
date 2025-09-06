package marketplace.User.Auth.Service.controller;

import lombok.RequiredArgsConstructor;
import marketplace.User.Auth.Service.dto.request.UpdateUserRequest;
import marketplace.User.Auth.Service.dto.response.UserResponse;
import marketplace.User.Auth.Service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/update")
    public ResponseEntity<UserResponse> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                                   @RequestBody UpdateUserRequest request) {
        String email = userDetails.getUsername();

        UserResponse response = userService.updateUser(email, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get")
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        UserResponse response = userService.getUser(email);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<UserResponse> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        UserResponse response = userService.deleteUser(email);

        return ResponseEntity.ok(response);
    }

}
