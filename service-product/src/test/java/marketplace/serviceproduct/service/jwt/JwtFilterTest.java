package marketplace.serviceproduct.service.jwt;

import com.marketplace.serviceProduct.service.jwt.JwtFilter;
import com.marketplace.serviceProduct.service.jwt.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter(jwtService);
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldSetAuthentication() throws Exception {
        String validToken = "valid.jwt.token";
        String email = "test@example.com";
        Collection<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenReturn(email);
        when(jwtService.isTokenValid(validToken)).thenReturn(true);
        when(jwtService.extractAuthorities(validToken)).thenReturn((Collection) authorities);

        invokeDoFilterInternal();

        verify(jwtService).extractUsername(validToken);
        verify(jwtService).isTokenValid(validToken);
        verify(jwtService).extractAuthorities(validToken);
        verify(filterChain).doFilter(request, response);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(email, authentication.getPrincipal());
        assertEquals(authorities, authentication.getAuthorities());
    }

    @Test
    void doFilterInternal_WithNoAuthHeader_ShouldContinueFilterChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        invokeDoFilterInternal();

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void doFilterInternal_WithInvalidAuthHeader_ShouldContinueFilterChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        invokeDoFilterInternal();

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldContinueFilterChain() throws Exception {
        String invalidToken = "invalid.jwt.token";
        String email = "test@example.com";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(jwtService.extractUsername(invalidToken)).thenReturn(email);
        when(jwtService.isTokenValid(invalidToken)).thenReturn(false);

        invokeDoFilterInternal();

        verify(jwtService).extractUsername(invalidToken);
        verify(jwtService).isTokenValid(invalidToken);
        verify(filterChain).doFilter(request, response);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
    }

    @Test
    void doFilterInternal_WithNullEmail_ShouldContinueFilterChain() throws Exception {
        String token = "token.with.null.email";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(null);

        invokeDoFilterInternal();

        verify(jwtService).extractUsername(token);
        verifyNoMoreInteractions(jwtService);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithExistingAuthentication_ShouldContinueFilterChain() throws Exception {
        String token = "valid.token";
        String email = "test@example.com";

        Authentication existingAuth = new UsernamePasswordAuthenticationToken(
                "existing@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(email);

        invokeDoFilterInternal();

        verify(jwtService).extractUsername(token);
        verifyNoMoreInteractions(jwtService);
        verify(filterChain).doFilter(request, response);

        assertEquals(existingAuth, SecurityContextHolder.getContext().getAuthentication());
    }

    private void invokeDoFilterInternal() throws Exception {
        Method method = JwtFilter.class.getDeclaredMethod(
                "doFilterInternal", HttpServletRequest.class, HttpServletResponse.class, FilterChain.class
        );
        method.setAccessible(true);
        method.invoke(jwtFilter, request, response, filterChain);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

}