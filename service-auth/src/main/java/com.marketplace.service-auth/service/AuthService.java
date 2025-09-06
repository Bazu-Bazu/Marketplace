package marketplace.User.Auth.Service.service;

import lombok.RequiredArgsConstructor;
import marketplace.User.Auth.Service.dto.CustomUserDetails;
import marketplace.User.Auth.Service.dto.request.LoginUserRequest;
import marketplace.User.Auth.Service.dto.request.RegisterUserRequest;
import marketplace.User.Auth.Service.dto.request.VerifyUserRequest;
import marketplace.User.Auth.Service.dto.response.AuthResponse;
import marketplace.User.Auth.Service.entity.RefreshToken;
import marketplace.User.Auth.Service.entity.User;
import marketplace.User.Auth.Service.exception.AccountNotVerifiedException;
import marketplace.User.Auth.Service.exception.AccountVerifiedException;
import marketplace.User.Auth.Service.exception.InvalidTokenException;
import marketplace.User.Auth.Service.exception.InvalidVerificationCodeException;
import marketplace.User.Auth.Service.repository.RefreshTokenRepository;
import marketplace.User.Auth.Service.repository.UserRepository;
import marketplace.User.Auth.Service.service.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationCodeService verificationCodeService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse signUp(RegisterUserRequest request) {
        userService.registerUser(request);

        String email = request.getEmail();

        verificationCodeService.sendVerificationCode(email);

        return AuthResponse.builder()
                .message("Verification code sent to your email.")
                .email(email)
                .build();

    }

    public AuthResponse authenticate(LoginUserRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!user.isEnabled()) {
            throw new AccountNotVerifiedException("Account not verified.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        refreshTokenService.addRefreshToken(user, refreshToken);

        return AuthResponse.builder()
                .message("You are logged in to your account.")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public AuthResponse verifyUser(VerifyUserRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (verificationCodeService.verifyCode(request.getEmail(), request.getVerificationCode())) {
            user.setEnabled(true);
            userRepository.save(user);

            return AuthResponse.builder()
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

    public AuthResponse updateTokens(String refreshToken) {
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

        UserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        refreshTokenService.addRefreshToken(user, newRefreshToken);

        refreshTokenService.deleteByToken(storedToken);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public AuthResponse logout(String accessToken) {
        String email = jwtService.extractUsername(accessToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        refreshTokenService.deleteByUser(user);

        return AuthResponse.builder()
                .message("You have logged out of your account.")
                .build();
    }

}
