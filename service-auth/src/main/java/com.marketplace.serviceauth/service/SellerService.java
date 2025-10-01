package com.marketplace.serviceauth.service;

import com.marketplace.serviceauth.dto.request.RegisterSellerRequest;
import com.marketplace.serviceauth.dto.request.UpdateSellerRequest;
import com.marketplace.serviceauth.dto.response.SellerResponse;
import com.marketplace.serviceauth.entity.Seller;
import com.marketplace.serviceauth.entity.User;
import com.marketplace.serviceauth.enums.Role;
import com.marketplace.serviceauth.exception.SellerException;
import com.marketplace.serviceauth.repository.SellerRepository;
import com.marketplace.serviceauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public SellerResponse registerSeller(String email, RegisterSellerRequest request) {
        User user = userService.findUserByEmail(email);

        if (user.getRole() == Role.ROLE_SELLER) {
            throw new SellerException("Seller with this email already exists.");
        }

        Seller seller = new Seller();
        seller.setUser(user);
        seller.setName(request.getName());
        seller.setEmail(email);
        sellerRepository.save(seller);

        userService.updateIfNotNullAndSave(
                Role.ROLE_SELLER,
                user::setRole,
                () -> userRepository.save(user)
        );

        return buildSellerResponse(seller);
    }

    @Transactional
    public SellerResponse updateSeller(String email, UpdateSellerRequest request) {
        Seller seller = findSellerByEmail(email);

        User user = userService.findUserByEmail(email);

        String newEmail = request.getEmail();
        if (newEmail != null) {
            seller.setEmail(newEmail);

            userService.updateIfNotNullAndSave(
                    newEmail,
                    user::setEmail,
                    () -> userRepository.save(user)
            );
        }

        if (request.getName() != null) {
            seller.setName(request.getName());
        }

        sellerRepository.save(seller);

        return buildSellerResponse(seller);
    }

    public SellerResponse getSeller(String email) {
        Seller seller = findSellerByEmail(email);

        return buildSellerResponse(seller);
    }

    @Transactional
    public void deleteSeller(String email) {
        Seller seller = findSellerByEmail(email);

        User user = userService.findUserByEmail(email);

        sellerRepository.delete(seller);

        userService.updateIfNotNullAndSave(
                Role.ROLE_USER,
                user::setRole,
                () -> userRepository.save(user)
        );
    }

    public Seller findSellerByEmail(String email) {
        return sellerRepository.findByEmail(email)
                .orElseThrow(() -> new SellerException("Seller not found."));
    }

    private SellerResponse buildSellerResponse(Seller seller) {
        return SellerResponse.builder()
                .id(seller.getId())
                .name(seller.getName())
                .email(seller.getEmail())
                .build();
    }

}
