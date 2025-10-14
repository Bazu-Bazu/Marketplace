package marketplace.serviceproduct.service.jwt;

import com.marketplace.serviceProduct.exception.HttpServletRequestException;
import com.marketplace.serviceProduct.service.jwt.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        setJwtSecret("mySuperSecretKeyThatIsLongEnoughForHS512Algorithm12345");
    }

    @Test
    void extractAuthorities_WithValidTokenAndAuthorities_ShouldReturnAuthorities() {
        String token = generateTestTokenWithAuthorities(List.of("ROLE_USER", "ROLE_ADMIN"));

        Collection<? extends GrantedAuthority> authorities = jwtService.extractAuthorities(token);

        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void extractAuthorities_WithValidTokenNoAuthorities_ShouldReturnEmptyList() {
        String token = generateTestTokenWithoutAuthorities();

        Collection<? extends GrantedAuthority> authorities = jwtService.extractAuthorities(token);

        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void extractUsername_WithValidToken_ShouldReturnUsername() {
        String expectedUsername = "test@example.com";
        String token = generateTestTokenWithUsername(expectedUsername);

        String username = jwtService.extractUsername(token);

        assertEquals(expectedUsername, username);
    }

    @Test
    void isTokenValid_WithValidToken_ShouldReturnTrue() {
        String token = generateTestTokenWithUsername("test@example.com");

        boolean isValid = jwtService.isTokenValid(token);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WithInvalidToken_ShouldReturnFalse() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtService.isTokenValid(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void extractSellerId_WithValidToken_ShouldReturnSellerId() {
        Long expectedSellerId = 123L;
        String token = generateTestTokenWithSellerId(expectedSellerId);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        Long sellerId = jwtService.extractSellerId(request);

        assertEquals(expectedSellerId, sellerId);
    }

    @Test
    void extractSellerId_WithNoAuthHeader_ShouldThrowException() {
        when(request.getHeader("Authorization")).thenReturn(null);

        HttpServletRequestException exception = assertThrows(HttpServletRequestException.class,
                () -> jwtService.extractSellerId(request));

        assertEquals("JWT token not found in Authorization header", exception.getMessage());
    }

    @Test
    void extractSellerName_WithValidToken_ShouldReturnSellerName() {
        String expectedSellerName = "Test Seller";
        String token = generateTestTokenWithSellerName(expectedSellerName);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        String sellerName = jwtService.extractSellerName(request);

        assertEquals(expectedSellerName, sellerName);
    }

    @Test
    void extractSellerName_WithInvalidAuthHeader_ShouldThrowException() {
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        HttpServletRequestException exception = assertThrows(HttpServletRequestException.class,
                () -> jwtService.extractSellerName(request));

        assertEquals("JWT token not found in Authorization header", exception.getMessage());
    }

    private String generateTestTokenWithAuthorities(List<String> authorities) {
        return Jwts.builder()
                .subject("test@example.com")
                .claim("authorities", authorities)
                .signWith(getTestSignInKey())
                .compact();
    }

    private String generateTestTokenWithoutAuthorities() {
        return Jwts.builder()
                .subject("test@example.com")
                .signWith(getTestSignInKey())
                .compact();
    }

    private String generateTestTokenWithUsername(String username) {
        return Jwts.builder()
                .subject(username)
                .signWith(getTestSignInKey())
                .compact();
    }

    private String generateTestTokenWithSellerId(Long sellerId) {
        return Jwts.builder()
                .subject("test@example.com")
                .claim("seller_id", sellerId)
                .signWith(getTestSignInKey())
                .compact();
    }

    private String generateTestTokenWithSellerName(String sellerName) {
        return Jwts.builder()
                .subject("test@example.com")
                .claim("seller_name", sellerName)
                .signWith(getTestSignInKey())
                .compact();
    }

    private SecretKey getTestSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode("mySuperSecretKeyThatIsLongEnoughForHS512Algorithm12345");
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private void setJwtSecret(String secret) {
        try {
            Field field = JwtService.class.getDeclaredField("jwtSecret");
            field.setAccessible(true);
            field.set(jwtService, secret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
