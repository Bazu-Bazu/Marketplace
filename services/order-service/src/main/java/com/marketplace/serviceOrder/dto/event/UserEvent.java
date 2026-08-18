package com.marketplace.serviceOrder.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserEvent {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;

}
