package com.marketplace.serviceauth.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserEvent {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;

}
