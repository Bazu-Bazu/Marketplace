package com.example.Marketplace.service;

import com.example.Marketplace.dto.UserDto;
import com.example.Marketplace.model.User;
import com.example.Marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationCodeService verificationCodeService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public void startRegistration(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("The password must contain 8 or more characters");
        }

        verificationCodeService.sendVerificationCode(email);
    }

    public User finishRegistration(UserDto userDto, String code) {
        if (!verificationCodeService.verifyCode(userDto, code)) {
            throw new IllegalArgumentException("Incorrect code");
        }

        return userService.registerUser(userDto);
    }

}
