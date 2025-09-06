package marketplace.User.Auth.Service.service;

import lombok.RequiredArgsConstructor;
import marketplace.User.Auth.Service.dto.request.RegisterSellerRequest;
import marketplace.User.Auth.Service.dto.request.UpdateSellerRequest;
import marketplace.User.Auth.Service.dto.response.SellerResponse;
import marketplace.User.Auth.Service.entity.Seller;
import marketplace.User.Auth.Service.entity.User;
import marketplace.User.Auth.Service.enums.Role;
import marketplace.User.Auth.Service.exception.SellerNotFoundException;
import marketplace.User.Auth.Service.repository.SellerRepository;
import marketplace.User.Auth.Service.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    @Transactional
    public SellerResponse registerSeller(String email, RegisterSellerRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (user.getRole() == Role.ROLE_SELLER) {
            return SellerResponse.builder()
                    .message("You already as a seller.")
                    .build();
        }

        Seller seller = new Seller();
        seller.setUser(user);
        seller.setName(request.getName());
        seller.setEmail(email);
        sellerRepository.save(seller);

        user.setRole(Role.ROLE_SELLER);
        userRepository.save(user);

        return SellerResponse.builder()
                .message("You have successfully registered as a seller.")
                .build();
    }

    @Transactional
    public SellerResponse updateSeller(String email, UpdateSellerRequest request) {
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new SellerNotFoundException("Seller not found."));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (request.getEmail() != null) {
            seller.setEmail(request.getEmail());
            user.setEmail(request.getEmail());

            userRepository.save(user);
        }

        if (request.getName() != null) {
            seller.setName(request.getName());
        }

        sellerRepository.save(seller);

        return SellerResponse.builder()
                .message("Your data has been successfully updated.")
                .build();
    }

    public SellerResponse getSeller(String email) {
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new SellerNotFoundException("Seller not found."));

        return SellerResponse.builder()
                .name(seller.getName())
                .email(seller.getEmail())
                .build();
    }

    @Transactional
    public SellerResponse deleteSeller(String email) {
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new SellerNotFoundException("Seller not found."));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SellerNotFoundException("User not found."));

        sellerRepository.delete(seller);

        user.setRole(Role.ROLE_USER);
        userRepository.save(user);

        return SellerResponse.builder()
                .message("Your seller account has been successfully deleted")
                .build();
    }

}
