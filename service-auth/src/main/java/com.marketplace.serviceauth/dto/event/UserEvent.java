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

    public static UserEvent createTestEvent(Long id, String email, String firstName, String lastName) {
        return new UserEvent(id, email, firstName, lastName);
    }

}
