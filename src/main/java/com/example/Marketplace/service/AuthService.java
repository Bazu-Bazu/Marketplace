package com.example.Marketplace.service;

import com.example.Marketplace.dto.response.AuthResponse;
import com.example.Marketplace.dto.LoginUserDto;
import com.example.Marketplace.dto.RegisterUserDto;
import com.example.Marketplace.dto.VerifyUserDto;
import com.example.Marketplace.enums.Role;
import com.example.Marketplace.exception.*;
import com.example.Marketplace.model.RefreshToken;
import com.example.Marketplace.model.User;
import com.example.Marketplace.repository.RefreshTokenRepository;
import com.example.Marketplace.repository.UserRepository;
import com.example.Marketplace.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationCodeService verificationCodeService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public AuthResponse signUp(RegisterUserDto registerUserDto) {
        if (userRepository.existsByEmail(registerUserDto.getEmail())) {
            throw new UserAlreadyExistsException("The user with this email already exists.");
        }

        User user = new User();
        user.setFirstName(registerUserDto.getFirstName());
        user.setLastName(registerUserDto.getLastName());
        user.setEmail(registerUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setBirthDate(registerUserDto.getBirthDate());
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);

        verificationCodeService.sendVerificationCode(registerUserDto.getEmail());

        return AuthResponse.builder()
                .message("Verification code sent to your email.")
                .email(registerUserDto.getEmail())
                .build();

    }

    @Transactional
    public AuthResponse authenticate(LoginUserDto loginUserDto) {
        User user = userRepository.findByEmail(loginUserDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!user.isEnabled()) {
            throw new AccountNotVerifiedException("Account not verified.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUserDto.getEmail(),
                            loginUserDto.getPassword()
                    )
            );
        } catch (StackOverflowError e) {
            throw new AuthenticationServiceException("Authentication failed due to configuration error");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);

        return AuthResponse.builder()
                .message("You are logged in to your account")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public AuthResponse verifyUser(VerifyUserDto verifyUserDto) {
        User user = userRepository.findByEmail(verifyUserDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (verificationCodeService.verifyCode(verifyUserDto.getEmail(), verifyUserDto.getVerificationCode())) {
            user.setEnabled(true);
            userRepository.save(user);

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .email(user.getEmail())
                    .message("Account successfully verified.")
                    .build();
        }
        else {
            throw new InvalidVerificationCodeException("Invalid verification code.");
        }
    }

    public AuthResponse resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (user.isEnabled()) {
            throw new AccountVerifiedException("Account is already verified.");
        }

        verificationCodeService.sendVerificationCode(email);

        return AuthResponse.builder()
                .message("Verification code sent to your email again.")
                .email(email)
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Provided token is not a refresh token.");
        }

        String email = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found."));

        if (storedToken.isRevoked() || storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidTokenException("Refresh token is invalid or expired.");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        saveRefreshToken(user, newRefreshToken);

        refreshTokenRepository.delete(storedToken);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public AuthResponse logout(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);

        return AuthResponse.builder()
                .message("You have logged out of your account.")
                .build();
    }

    @Transactional
    public AuthResponse logoutAllSessions(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        refreshTokenRepository.deleteAllByUser(user);

        return AuthResponse.builder()
                .message("You have logged out of your account on all devices.")
                .build();
    }

    @Transactional
    protected void saveRefreshToken(User user, String token) {
        Instant expiresAt = Instant.now().plusMillis(jwtService.getRefreshTokenExpiration());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(expiresAt);
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);
    }

}
