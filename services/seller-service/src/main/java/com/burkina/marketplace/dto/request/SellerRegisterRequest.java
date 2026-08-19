package com.burkina.marketplace.dto.request;

import java.util.List;

public record SellerRegisterRequest(

        String name,

        List<String> phones,

        List<String> emails,

        String description,

        String avatarUrl,

        String inn,

        String address
) {}
