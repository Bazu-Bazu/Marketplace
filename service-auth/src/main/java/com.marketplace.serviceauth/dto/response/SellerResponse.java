package com.marketplace.serviceauth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SellerResponse {

    private Long id;
    private String name;
    private String email;

}
