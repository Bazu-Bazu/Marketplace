package com.burkina.marketplace.dto.request;

import com.burkina.marketplace.validation.annotation.UniqueInn;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SellerUpdateInfoRequest(

        @Pattern(regexp = "^[а-яА-Яa-zA-Z0-9_ ]+$", message = "Name must contain only letters, digits, spaces or _")
        @Size(min = 3, max = 30, message = "Name must have 3-30 characters")
        String name,

        @Size(max = 300, message = "Description must have before 300 characters")
        String description,

        String avatarUrl,

        @Pattern(regexp = "^\\d{10,12}$", message = "Invalid INN")
        @UniqueInn
        String inn,

        String address
) {}
