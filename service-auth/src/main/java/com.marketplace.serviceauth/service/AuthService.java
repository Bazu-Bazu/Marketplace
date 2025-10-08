package com.marketplace.serviceauth.service;

import com.marketplace.serviceauth.dto.CustomUserDetails;
import com.marketplace.serviceauth.dto.event.UserEvent;
import com.marketplace.serviceauth.dto.request.LoginUserRequest;
import com.marketplace.serviceauth.dto.request.RefreshTokenRequest;
import com.marketplace.serviceauth.dto.request.RegisterUserRequest;
import com.marketplace.serviceauth.dto.request.VerifyUserRequest;
import com.marketplace.serviceauth.dto.response.AuthResponse;
import com.marketplace.serviceauth.entity.RefreshToken;
import com.marketplace.serviceauth.entity.Seller;
import com.marketplace.serviceauth.entity.User;
import com.marketplace.serviceauth.enums.Role;
import com.marketplace.serviceauth.exception.AuthenticationException;
import com.marketplace.serviceauth.exception.RefreshTokenException;
import com.marketplace.serviceauth.exception.VerificationCodeException;
import com.marketplace.serviceauth.repository.RefreshTokenRepository;
import com.marketplace.serviceauth.repository.UserRepository;
import com.marketplace.serviceauth.service.event.UserEventPublisher;
import com.marketplace.serviceauth.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final SellerService sellerService;
    private final UserEventPublisher userEventPublisher;

    public void signUp(RegisterUserRequest request) {
        userService.registerUser(request);

        verificationCodeService.sendVerificationCode(request.getEmail());
    }

    public AuthResponse authenticate(LoginUserRequest request, String deviceInfo, String ipAddress) {
        User user = userService.findUserByEmail(request.getEmail());

        if (!user.isEnabled()) {
            throw new AuthenticationException("Account not verified.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = createUserDetails(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        refreshTokenService.addRefreshToken(user, refreshToken, deviceInfo, ipAddress);

        return buildAuthResponse(user.getId(), accessToken, refreshToken);
    }

    private UserDetails createUserDetails(User user) {
        UserDetails userDetails;
        if (user.getRole() == Role.ROLE_SELLER) {
            Seller seller = sellerService.findSellerByEmail(user.getEmail());

            userDetails = new CustomUserDetails(user, seller);
        }
        else {
            userDetails = new CustomUserDetails(user);
        }

        return userDetails;
    }

    @Transactional
    public AuthResponse verifyUser(VerifyUserRequest request) {
        User user = userService.findUserByEmail(request.getEmail());

        if (verificationCodeService.verifyCode(request.getEmail(), request.getVerificationCode())) {
            userService.updateIfNotNullAndSave(
                    true,
                    user::setEnabled,
                    () -> userRepository.save(user)
            );

            userEventPublisher.sendUserIdToKafka(buildUserEvent(user));

            return buildAuthResponse(user.getId(), null, null);
        }
        else {
            throw new VerificationCodeException("Invalid verification code.");
        }
    }

    private UserEvent buildUserEvent(User user) {
        return UserEvent.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public AuthResponse resendVerificationCode(String email) {
        User user = userService.findUserByEmail(email);

        if (user.isEnabled()) {
            throw new AuthenticationException("Account is already verified.");
        }

        verificationCodeService.sendVerificationCode(email);

        return buildAuthResponse(user.getId(), null, null);
    }

    public AuthResponse refresh(String refreshToken, String deviceInfo, String ipAddress) {
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new RefreshTokenException("Provided token is not a refresh token.");
        }

        String email = jwtService.extractUsername(refreshToken);
        User user = userService.findUserByEmail(email);

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RefreshTokenException("Refresh token not found."));

        refreshTokenService.verifyActivity(storedToken);
        refreshTokenService.revokeToken(refreshToken);

        if (!storedToken.getDeviceInfo().equals(deviceInfo)) {
            throw new AuthenticationException("Invalid device for refresh token.");
        }

        UserDetails userDetails = createUserDetails(user);
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        refreshTokenService.addRefreshToken(user, newRefreshToken, deviceInfo, ipAddress);

        return buildAuthResponse(user.getId(), newAccessToken, newRefreshToken);
    }

    public void logout(RefreshTokenRequest request) {
        refreshTokenService.revokeToken(request.getRefreshToken());
    }

    public void logoutAll(String email) {
        User user = userService.findUserByEmail(email);

        refreshTokenService.revokeAllUserTokens(user);
    }

    private AuthResponse buildAuthResponse(Long userId, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
