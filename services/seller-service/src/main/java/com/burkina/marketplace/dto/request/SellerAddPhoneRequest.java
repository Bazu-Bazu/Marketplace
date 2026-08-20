package com.burkina.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SellerAddPhoneRequest(

        @NotBlank(message = "Phone cannot be empty")
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid phone number format")
        String phone
) {}
