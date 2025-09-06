package marketplace.User.Auth.Service.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import marketplace.User.Auth.Service.dto.CustomUserDetails;
import marketplace.User.Auth.Service.entity.User;
import marketplace.User.Auth.Service.repository.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!user.isEnabled()) {
            throw new DisabledException("Email not verified.");
        }

        return new CustomUserDetails(user);
    }

}
