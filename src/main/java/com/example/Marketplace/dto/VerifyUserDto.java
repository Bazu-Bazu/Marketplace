package com.example.Marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyUserDto {

    @NotBlank
    private String email;

    @NotBlank
    private String verificationCode;

}
