package com.example.Marketplace.service;

import com.example.Marketplace.dto.response.SellerResponse;
import com.example.Marketplace.enums.Role;
import com.example.Marketplace.model.Seller;
import com.example.Marketplace.model.User;
import com.example.Marketplace.repository.SellerRepository;
import com.example.Marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    @Transactional
    public SellerResponse registerSeller(String email, String name) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (user.getRole() == Role.ROLE_SELLER) {
            return SellerResponse.builder()
                    .message("You already as a seller")
                    .build();
        }

        Seller seller = new Seller();
        seller.setUser(user);
        seller.setName(name);
        seller.setEmail(email);
        sellerRepository.save(seller);

        user.setRole(Role.ROLE_SELLER);
        userRepository.save(user);

        return SellerResponse.builder()
                .message("You have successfully registered as a seller")
                .build();
    }

}
