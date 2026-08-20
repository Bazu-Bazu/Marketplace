package com.burkina.marketplace.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SellerAddEmailRequest(

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email
) {}
