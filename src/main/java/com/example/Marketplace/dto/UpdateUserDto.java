package com.example.Marketplace.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserDto {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private LocalDate birthDate;

}
