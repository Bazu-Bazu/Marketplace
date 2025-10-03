package com.marketplace.serviceauth.service;

import com.marketplace.serviceauth.dto.CustomUserDetails;
import com.marketplace.serviceauth.entity.Seller;
import com.marketplace.serviceauth.entity.User;
import com.marketplace.serviceauth.enums.Role;
import com.marketplace.serviceauth.repository.SellerRepository;
import com.marketplace.serviceauth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_WithRegularUser_ShouldReturnUserDetails() {
        String email = "user@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setEnabled(true);
        user.setRole(Role.ROLE_USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof CustomUserDetails);
        verify(userRepository).findByEmail(email);
        verify(sellerRepository, never()).findByUserId(anyLong());
    }

    @Test
    void loadUserByUsername_WithSellerUser_ShouldReturnSellerUserDetails() {
        String email = "seller@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setEnabled(true);
        user.setRole(Role.ROLE_SELLER);

        Seller seller = new Seller();
        seller.setId(1L);
        seller.setEmail(email);
        seller.setUser(user);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(sellerRepository.findByUserId(user.getId())).thenReturn(Optional.of(seller));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof CustomUserDetails);
        verify(userRepository).findByEmail(email);
        verify(sellerRepository).findByUserId(user.getId());
    }

    @Test
    void loadUserByUsername_WithNonExistentUser_ShouldThrowException() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(email));

        assertEquals("User not found.", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(sellerRepository, never()).findByUserId(anyLong());
    }

    @Test
    void loadUserByUsername_WithDisabledUser_ShouldThrowException() {
        String email = "disabled@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setEnabled(false);
        user.setRole(Role.ROLE_USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        DisabledException exception = assertThrows(DisabledException.class,
                () -> customUserDetailsService.loadUserByUsername(email));

        assertEquals("Email not verified.", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(sellerRepository, never()).findByUserId(anyLong());
    }

    @Test
    void loadUserByUsername_WithSellerRoleButNoSeller_ShouldThrowException() {
        String email = "seller@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setEnabled(true);
        user.setRole(Role.ROLE_SELLER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(sellerRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(email));

        assertEquals("Seller not found.", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(sellerRepository).findByUserId(user.getId());
    }

    @Test
    void loadUserByUsername_WithAdminUser_ShouldReturnUserDetails() {
        String email = "admin@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setEnabled(true);
        user.setRole(Role.ROLE_ADMIN);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof CustomUserDetails);
        verify(userRepository).findByEmail(email);
        verify(sellerRepository, never()).findByUserId(anyLong());
    }

    @Test
    void loadUserByUsername_WithEnabledSeller_ShouldReturnSellerUserDetails() {
        String email = "enabled.seller@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setEnabled(true);
        user.setRole(Role.ROLE_SELLER);

        Seller seller = new Seller();
        seller.setId(1L);
        seller.setEmail(email);
        seller.setName("Test Seller");
        seller.setUser(user);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(sellerRepository.findByUserId(user.getId())).thenReturn(Optional.of(seller));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof CustomUserDetails);
        verify(userRepository).findByEmail(email);
        verify(sellerRepository).findByUserId(user.getId());
    }

    @Test
    void loadUserByUsername_WithDisabledSeller_ShouldThrowException() {
        String email = "disabled.seller@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setEnabled(false);
        user.setRole(Role.ROLE_SELLER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        DisabledException exception = assertThrows(DisabledException.class,
                () -> customUserDetailsService.loadUserByUsername(email));

        assertEquals("Email not verified.", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(sellerRepository, never()).findByUserId(anyLong());
    }

}