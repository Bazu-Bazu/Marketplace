package com.marketplace.serviceauth.service;

import com.marketplace.serviceauth.dto.request.RegisterUserRequest;
import com.marketplace.serviceauth.dto.request.UpdateUserRequest;
import com.marketplace.serviceauth.dto.response.UserResponse;
import com.marketplace.serviceauth.entity.User;
import com.marketplace.serviceauth.enums.Role;
import com.marketplace.serviceauth.exception.UserException;
import com.marketplace.serviceauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException("The user with this email already exists.");
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
        User user = findUserByEmail(email);

        updateIfNotNull(request.getFirstName(), user::setFirstName);
        updateIfNotNull(request.getLastName(), user::setLastName);
        updateIfNotNull(request.getEmail(), user::setEmail);
        updateIfNotNull(request.getPassword(), pwd ->
                user.setPassword(passwordEncoder.encode(pwd)));
        updateIfNotNull(request.getBirthDate(), user::setBirthDate);
        updateIfNotNull(request.getPhone(), user::setPhone);
        userRepository.save(user);

        return buildUserResponse(user);
    }

    private <T> void updateIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    public  <T> void updateIfNotNullAndSave(T value, Consumer<T> setter, Runnable saveAction) {
        if (value != null) {
            setter.accept(value);
            saveAction.run();
        }
    }

    public UserResponse getUser(String email) {
        User user = findUserByEmail(email);

        return buildUserResponse(user);
    }

    @Transactional
    public void deleteUser(String email) {
        User user = findUserByEmail(email);
        userRepository.delete(user);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    private UserResponse buildUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .birthDate(user.getBirthDate())
                .build();
    }

}
