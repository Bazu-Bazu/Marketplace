package com.example.Marketplace.service;

import com.example.Marketplace.dto.UserDto;
import com.example.Marketplace.model.User;
import com.example.Marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

        verificationCodeService.sendVerificationCode(email);
    }

    public User finishRegistration(UserDto userDto, String code) {
        if (!verificationCodeService.verifyCode(userDto.getEmail(), code)) {
            throw new IllegalArgumentException("Incorrect code");
        }

        return userService.registerUser(userDto);
    }

    public void startLogin(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("There is no user with this email address"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new DisabledException("Invalid password");
        }

        verificationCodeService.sendVerificationCode(email);
    }

    public Authentication finishLogin(String email, String code) {
        if (!verificationCodeService.verifyCode(email, code)) {
            throw new IllegalArgumentException("Incorrect code");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                user.getAuthorities()
        );
    }

}
