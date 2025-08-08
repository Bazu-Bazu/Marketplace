package com.example.Marketplace.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private LocalDate birthDate;

}
