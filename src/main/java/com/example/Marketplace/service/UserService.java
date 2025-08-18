package com.example.Marketplace.service;

import com.example.Marketplace.dto.UpdateUserDto;
import com.example.Marketplace.model.User;
import com.example.Marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User updateUser(String email, UpdateUserDto updateUserDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (updateUserDto.getFirstName() != null) {
            user.setFirstName(updateUserDto.getFirstName());
        }

        if (updateUserDto.getLastName() != null) {
            user.setLastName(updateUserDto.getLastName());
        }

        if (updateUserDto.getEmail() != null) {
            user.setEmail(user.getEmail());
        }

        if (updateUserDto.getPassword() != null) {
            user.setPassword(user.getPassword());
        }

        if (updateUserDto.getBirthDate() != null) {
            user.setBirthDate(updateUserDto.getBirthDate());
        }

        if (updateUserDto.getPhone() != null) {
            user.setPhone(updateUserDto.getPhone());
        }

        return userRepository.save(user);
    }

}
