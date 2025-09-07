package com.marketplace.serviceauth.service;

import com.marketplace.serviceauth.dto.CustomUserDetails;
import com.marketplace.serviceauth.entity.Seller;
import com.marketplace.serviceauth.entity.User;
import com.marketplace.serviceauth.enums.Role;
import com.marketplace.serviceauth.repository.SellerRepository;
import com.marketplace.serviceauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!user.isEnabled()) {
            throw new DisabledException("Email not verified.");
        }

        if (user.getRole() == Role.ROLE_SELLER) {
            Seller seller = sellerRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Seller not found."));

            return new CustomUserDetails(user, seller);
        }

        return new CustomUserDetails(user);
    }

}
