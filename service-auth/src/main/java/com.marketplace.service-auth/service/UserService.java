package marketplace.User.Auth.Service.service;

import lombok.RequiredArgsConstructor;
import marketplace.User.Auth.Service.dto.request.RegisterUserRequest;
import marketplace.User.Auth.Service.dto.request.UpdateUserRequest;
import marketplace.User.Auth.Service.dto.response.UserResponse;
import marketplace.User.Auth.Service.entity.User;
import marketplace.User.Auth.Service.enums.Role;
import marketplace.User.Auth.Service.exception.UserAlreadyExistsException;
import marketplace.User.Auth.Service.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("The user with this email already exists.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBirthDate(request.getBirthDate());
        user.setCreatedAt(LocalDate.now());
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);
    }

    @Transactional
    public UserResponse updateUser(String email, UpdateUserRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getEmail() != null) {
            user.setEmail(user.getEmail());
        }

        if (request.getPassword() != null) {
            user.setPassword(user.getPassword());
        }

        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        userRepository.save(user);

        return UserResponse
                .builder()
                .message("Your data has been successfully updated.")
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .build();
    }

    public UserResponse getUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        return UserResponse
                .builder()
                .message("Your personal information.")
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .build();
    }

    @Transactional
    public UserResponse deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        userRepository.delete(user);

        return UserResponse.builder()
                .message("Your account has been successfully deleted.")
                .build();
    }

}
