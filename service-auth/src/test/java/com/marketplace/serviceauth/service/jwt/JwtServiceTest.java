package com.marketplace.serviceauth.service.jwt;

import com.marketplace.serviceauth.dto.CustomUserDetails;
import com.marketplace.serviceauth.entity.Seller;
import com.marketplace.serviceauth.entity.User;
import com.marketplace.serviceauth.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    private JwtService jwtService;

    private final String secretKey = "veryLongSecretKeyThatIsAtLeast64BytesLongForHmacSha512Algorithm1234567890";
    private final long accessTokenExpiration = 900000L;
    private final long refreshTokenExpiration = 604800000L;

    private User regularUser;
    private User sellerUser;
    private Seller seller;
    private CustomUserDetails regularUserDetails;
    private CustomUserDetails sellerUserDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "jwtSecret", secretKey);
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", accessTokenExpiration);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", refreshTokenExpiration);

        regularUser = new User();
        regularUser.setId(1L);
        regularUser.setEmail("regularUserEmail");
        regularUser.setPassword("password");
        regularUser.setRole(Role.ROLE_USER);
        regularUser.setEnabled(true);

        sellerUser = new User();
        sellerUser.setId(2L);
        sellerUser.setEmail("sellerUserEmail");
        sellerUser.setPassword("password");
        sellerUser.setRole(Role.ROLE_SELLER);
        sellerUser.setEnabled(true);

        seller = new Seller();
        seller.setId(10L);
        seller.setEmail("sellerUserEmail");
        seller.setName("Company");
        seller.setUser(sellerUser);

        regularUserDetails = new CustomUserDetails(regularUser);
        sellerUserDetails = new CustomUserDetails(sellerUser, seller);
    }

    @Test
    void generateAccessToken_ShouldGenerateValidToken() {
        String token = jwtService.generateAccessToken(regularUserDetails);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo(regularUser.getEmail());
        assertThat(jwtService.isTokenValid(token, regularUserDetails)).isTrue();
    }

    @Test
    void generateAccessToken_ForSeller_ShouldIncludeSellerId() {
        String token = jwtService.generateAccessToken(sellerUserDetails);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo(sellerUser.getEmail());

        Collection<? extends SimpleGrantedAuthority> authorities =
                (Collection<? extends SimpleGrantedAuthority>) jwtService.extractAuthorities(token);
        assertThat(authorities)
                .extracting("authority")
                .containsExactly("ROLE_SELLER");
    }

    @Test
    void generateRefreshToken_ShouldGenerateValidToken() {
        String token = jwtService.generateRefreshToken(regularUserDetails);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo(regularUser.getEmail());
        assertThat(jwtService.isRefreshToken(token)).isTrue();
    }

    @Test
    void extractUsername_ShouldReturnEmail() {
        String token = jwtService.generateAccessToken(regularUserDetails);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo(regularUser.getEmail());
    }

    @Test
    void extractUsername_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "invalid.token.here";

        assertThatThrownBy(() -> jwtService.extractUsername(invalidToken))
                .isInstanceOf(Exception.class);
    }

    @Test
    void extractAuthorities_ShouldReturnUserRoles() {
        String token = jwtService.generateAccessToken(regularUserDetails);

        Collection<? extends SimpleGrantedAuthority> authorities =
                (Collection<? extends SimpleGrantedAuthority>) jwtService.extractAuthorities(token);

        assertThat(authorities)
                .hasSize(1)
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void extractAuthorities_WhenNoAuthoritiesInToken_ShouldReturnEmptyList() {
        String token = jwtService.generateRefreshToken(regularUserDetails);

        Collection<? extends SimpleGrantedAuthority> authorities =
                (Collection<? extends SimpleGrantedAuthority>) jwtService.extractAuthorities(token);

        assertThat(authorities).isEmpty();
    }

    @Test
    void isTokenValid_WithValidToken_ShouldReturnTrue() {
        String token = jwtService.generateAccessToken(regularUserDetails);

        boolean isValid = jwtService.isTokenValid(token, regularUserDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    void isTokenValid_WithWrongUser_ShouldReturnFalse() {
        String token = jwtService.generateAccessToken(regularUserDetails);

        User differentUser = new User();
        differentUser.setId(3L);
        differentUser.setEmail("differentUserEmail");
        differentUser.setPassword("password");
        differentUser.setRole(Role.ROLE_USER);
        differentUser.setEnabled(true);

        CustomUserDetails differentUserDetails = new CustomUserDetails(differentUser);

        boolean isValid = jwtService.isTokenValid(token, differentUserDetails);

        assertThat(isValid).isFalse();
    }

    @Test
    void isRefreshToken_WithRefreshToken_ShouldReturnTrue() {
        String refreshToken = jwtService.generateRefreshToken(regularUserDetails);

        boolean isRefresh = jwtService.isRefreshToken(refreshToken);

        assertThat(isRefresh).isTrue();
    }

    @Test
    void isRefreshToken_WithAccessToken_ShouldReturnFalse() {
        String accessToken = jwtService.generateAccessToken(regularUserDetails);

        boolean isRefresh = jwtService.isRefreshToken(accessToken);

        assertThat(isRefresh).isFalse();
    }

    @Test
    void generateAccessToken_ForSeller_ShouldIncludeCustomClaims() {
        String token = jwtService.generateAccessToken(sellerUserDetails);

        String userId = jwtService.extractClaim(token, claims ->
                claims.get("user_id", Long.class)).toString();
        String sellerId = jwtService.extractClaim(token, claims ->
                claims.get("seller_id", Long.class)).toString();
        String sellerName = jwtService.extractClaim(token, claims ->
                claims.get("seller_name", String.class));

        assertThat(userId).isEqualTo("2");
        assertThat(sellerId).isEqualTo("10");
        assertThat(sellerName).isEqualTo("Company");
    }

    @Test
    void generateAccessToken_ForRegularUser_ShouldNotIncludeSellerId() {
        String token = jwtService.generateAccessToken(regularUserDetails);

        String userId = jwtService.extractClaim(token, claims ->
                claims.get("user_id", Long.class)).toString();
        Object sellerId = jwtService.extractClaim(token, claims -> claims.get("seller_id"));

        assertThat(userId).isEqualTo("1");
        assertThat(sellerId).isNull();
    }

    @Test
    void isTokenExpired_WithFreshToken_ShouldReturnFalse() {
        String token = jwtService.generateAccessToken(regularUserDetails);

        boolean isExpired = ReflectionTestUtils.invokeMethod(jwtService, "isTokenExpired", token);

        assertThat(isExpired).isFalse();
    }

    @Test
    void extractExpiration_ShouldReturnFutureDate() {
        String token = jwtService.generateAccessToken(regularUserDetails);

        java.util.Date expiration = ReflectionTestUtils.invokeMethod(jwtService, "extractExpiration", token);

        assertThat(expiration).isAfter(new java.util.Date());
    }

}

