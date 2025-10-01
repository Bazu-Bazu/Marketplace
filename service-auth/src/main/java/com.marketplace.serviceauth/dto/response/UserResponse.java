package com.marketplace.serviceauth.dto.response;

import com.marketplace.serviceauth.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private String phone;
    private LocalDate birthDate;

}
