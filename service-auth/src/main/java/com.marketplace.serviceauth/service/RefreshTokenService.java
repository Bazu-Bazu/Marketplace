package com.marketplace.serviceauth.service;

import com.marketplace.serviceauth.entity.RefreshToken;
import com.marketplace.serviceauth.entity.User;
import com.marketplace.serviceauth.exception.RefreshTokenException;
import com.marketplace.serviceauth.repository.RefreshTokenRepository;
import com.marketplace.serviceauth.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public void addRefreshToken(User user, String token, String deviceInfo, String ipAddress) {
        Instant expiresAt = Instant.now().plusMillis(jwtService.getRefreshTokenExpiration());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(expiresAt);
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setDeviceInfo(deviceInfo);
        refreshToken.setIpAddress(ipAddress);

        refreshTokenRepository.save(refreshToken);
    }

    public void verifyActivity(RefreshToken token) {
        if (!token.isActive()) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException("Refresh token was expired.");
        }
    }

    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshToken -> {
                    refreshToken.setRevokedAt(Instant.now());
                    refreshTokenRepository.save(refreshToken);
                });
    }

    @Transactional
    public void revokeAllUserTokens(User user) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUserId(user.getId());
        tokens.forEach(token -> token.setRevokedAt(Instant.now()));
        refreshTokenRepository.saveAll(tokens);
    }

    @Transactional
    public void revokeTokenByDevice(User user, String deviceInfo) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllActiveByUserIdAndDeviceInfo(user.getId(), deviceInfo);
        tokens.forEach(token -> token.setRevokedAt(Instant.now()));
        refreshTokenRepository.saveAll(tokens);
    }

}
