package com.marketplace.serviceauth.service.jwt;

import com.marketplace.serviceauth.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtFilter jwtFilter;

    private final String token = "validToken";
    private final String email = "userEmail";
    private final String password = "password";

    @BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter(jwtService, customUserDetailsService);
    }

    @Test
    void doFilterInternal_WhenNoAuthorizationHeader_ShouldContinueFilterChain() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, customUserDetailsService);
    }

    @Test
    void doFilterInternal_WhenInvalidAuthorizationHeader_ShouldContinueFilterChain()
            throws ServletException, IOException
    {
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, customUserDetailsService);
    }

    @Test
    void doFilterInternal_WhenValidToken_ShouldSetAuthentication() throws ServletException, IOException {
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        UserDetails userDetails = User.builder()
                .username(email)
                .password(password)
                .authorities(authorities)
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(email);
        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);
        when(jwtService.extractAuthorities(token)).thenReturn((Collection) authorities);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService).extractUsername(token);
        verify(customUserDetailsService).loadUserByUsername(email);
        verify(jwtService).isTokenValid(token, userDetails);
        verify(jwtService).extractAuthorities(token);
    }

    @Test
    void doFilterInternal_WhenExpiredToken_ShouldSetExpiredAttribute() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(email);
        when(customUserDetailsService.loadUserByUsername(email))
                .thenThrow(new io.jsonwebtoken.ExpiredJwtException(null, null, "Token expired"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(request).setAttribute(eq("expired"), any(io.jsonwebtoken.ExpiredJwtException.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WhenInvalidTokenInUserDetails_ShouldReturnUnauthorized()
            throws ServletException, IOException
    {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(email);
        when(customUserDetailsService.loadUserByUsername(email)).thenThrow(new RuntimeException("Invalid token"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WhenInvalidTokenInExtractUsername_ShouldThrowException()
            throws ServletException, IOException
    {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenThrow(new RuntimeException("Invalid token"));

        assertThrows(RuntimeException.class, () -> {
            jwtFilter.doFilterInternal(request, response, filterChain);
        });

        verify(filterChain, never()).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void doFilterInternal_WhenTokenInvalid_ShouldNotSetAuthentication() throws ServletException, IOException {
        UserDetails userDetails = User.builder()
                .username(email)
                .password(password)
                .authorities(Collections.emptyList())
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(email);
        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService).isTokenValid(token, userDetails);
        verify(jwtService, never()).extractAuthorities(token);
        verify(filterChain).doFilter(request, response);
    }

}
