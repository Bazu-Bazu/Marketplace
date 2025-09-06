package marketplace.User.Auth.Service.service;

import lombok.RequiredArgsConstructor;
import marketplace.User.Auth.Service.entity.RefreshToken;
import marketplace.User.Auth.Service.entity.User;
import marketplace.User.Auth.Service.repository.RefreshTokenRepository;
import marketplace.User.Auth.Service.service.jwt.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public void addRefreshToken(User user, String token) {
        Instant expiresAt = Instant.now().plusMillis(jwtService.getRefreshTokenExpiration());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(expiresAt);
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void deleteByToken(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteAllByUser(user);
    }
}
