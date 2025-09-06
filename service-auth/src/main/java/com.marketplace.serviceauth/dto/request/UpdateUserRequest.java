package com.marketplace.serviceauth.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private LocalDate birthDate;

}
