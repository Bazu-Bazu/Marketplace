package com.burkina.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SellerAddBankAccountRequest(

        @NotBlank(message = "Bank name cannot be empty")
        @Size(max = 100, message = "Bank name cannot exceed 100 characters")
        String bankName,

        @NotBlank(message = "Account number cannot be empty")
        @Pattern(regexp = "^[0-9]{20}$", message = "Account number must contain 20 digits")
        String accountNumber
) {}
