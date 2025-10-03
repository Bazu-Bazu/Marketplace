package com.marketplace.serviceauth.service;

import com.marketplace.serviceauth.entity.RefreshToken;
import com.marketplace.serviceauth.entity.User;
import com.marketplace.serviceauth.exception.RefreshTokenException;
import com.marketplace.serviceauth.repository.RefreshTokenRepository;
import com.marketplace.serviceauth.service.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User testUser;
    private RefreshToken activeToken;
    private RefreshToken expiredToken;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("userEmail");

        activeToken = new RefreshToken();
        activeToken.setId(1L);
        activeToken.setToken("activeToken");
        activeToken.setUser(testUser);
        activeToken.setExpiresAt(Instant.now().plusSeconds(3600));
        activeToken.setCreatedAt(Instant.now());
        activeToken.setRevokedAt(null);

        expiredToken = new RefreshToken();
        expiredToken.setId(2L);
        expiredToken.setToken("expiredToken");
        expiredToken.setUser(testUser);
        expiredToken.setExpiresAt(Instant.now().minusSeconds(3600));
        expiredToken.setCreatedAt(Instant.now().minusSeconds(7200));
        expiredToken.setRevokedAt(null);
    }

    @Test
    void addRefreshToken_ShouldSaveTokenWithCorrectData() {
        String token = "test-refresh-token";
        String deviceInfo = "Chrome on Windows";
        String ipAddress = "192.168.1.1";

        when(jwtService.getRefreshTokenExpiration()).thenReturn(5000L);
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        refreshTokenService.addRefreshToken(testUser, token, deviceInfo, ipAddress);

        verify(jwtService).getRefreshTokenExpiration();
        verify(refreshTokenRepository).save(argThat(savedToken ->
                savedToken.getToken().equals(token) &&
                        savedToken.getUser().equals(testUser) &&
                        savedToken.getDeviceInfo().equals(deviceInfo) &&
                        savedToken.getIpAddress().equals(ipAddress) &&
                        savedToken.getExpiresAt().isAfter(Instant.now()) &&
                        savedToken.getCreatedAt() != null
        ));
    }

    @Test
    void verifyActivity_WithActiveToken_ShouldNotThrowException() {
        assertDoesNotThrow(() -> refreshTokenService.verifyActivity(activeToken));
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }

    @Test
    void verifyActivity_WithExpiredToken_ShouldThrowExceptionAndDeleteToken() {
        RefreshTokenException exception = assertThrows(
                RefreshTokenException.class,
                () -> refreshTokenService.verifyActivity(expiredToken)
        );

        assertEquals("Refresh token was expired.", exception.getMessage());
        verify(refreshTokenRepository).delete(expiredToken);
    }

    @Test
    void verifyActivity_WithRevokedToken_ShouldThrowExceptionAndDeleteToken() {
        RefreshToken revokedToken = new RefreshToken();
        revokedToken.setId(3L);
        revokedToken.setToken("revokedToken");
        revokedToken.setUser(testUser);
        revokedToken.setExpiresAt(Instant.now().plusSeconds(3600));
        revokedToken.setCreatedAt(Instant.now());
        revokedToken.setRevokedAt(Instant.now());
        revokedToken.setDeviceInfo("testDevice");
        revokedToken.setIpAddress("127.8.8.8");

        RefreshTokenException exception = assertThrows(
                RefreshTokenException.class,
                () -> refreshTokenService.verifyActivity(revokedToken)
        );

        assertEquals("Refresh token was expired.", exception.getMessage());
        verify(refreshTokenRepository).delete(revokedToken);
    }

    @Test
    void revokeToken_WithExistingToken_ShouldRevokeToken() {
        String token = "existing-token";
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(activeToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(activeToken);

        refreshTokenService.revokeToken(token);

        verify(refreshTokenRepository).findByToken(token);
        verify(refreshTokenRepository).save(argThat(savedToken ->
                savedToken.getRevokedAt() != null
        ));
    }

    @Test
    void revokeToken_WithNonExistingToken_ShouldDoNothing() {
        String token = "non-existing-token";
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        refreshTokenService.revokeToken(token);

        verify(refreshTokenRepository).findByToken(token);
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void revokeAllUserTokens_ShouldRevokeAllUserTokens() {
        List<RefreshToken> userTokens = List.of(activeToken, expiredToken);
        when(refreshTokenRepository.findAllByUserId(testUser.getId())).thenReturn(userTokens);
        when(refreshTokenRepository.saveAll(any())).thenReturn(userTokens);
        refreshTokenService.revokeAllUserTokens(testUser);

        verify(refreshTokenRepository).findAllByUserId(testUser.getId());

        assertTrue(userTokens.stream().allMatch(token -> token.getRevokedAt() != null));

        verify(refreshTokenRepository).saveAll(userTokens);
    }

    @Test
    void revokeTokenByDevice_ShouldRevokeOnlySpecificDeviceTokens() {
        String deviceInfo = "Mobile Safari";
        List<RefreshToken> deviceTokens = List.of(activeToken);

        when(refreshTokenRepository.findAllActiveByUserIdAndDeviceInfo(testUser.getId(), deviceInfo))
                .thenReturn(deviceTokens);
        when(refreshTokenRepository.saveAll(any())).thenReturn(deviceTokens);

        refreshTokenService.revokeTokenByDevice(testUser, deviceInfo);

        verify(refreshTokenRepository).findAllActiveByUserIdAndDeviceInfo(testUser.getId(), deviceInfo);

        assertNotNull(activeToken.getRevokedAt());

        verify(refreshTokenRepository).saveAll(deviceTokens);
    }

}