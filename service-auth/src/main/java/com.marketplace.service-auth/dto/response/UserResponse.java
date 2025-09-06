package marketplace.User.Auth.Service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserResponse {

    private String message;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate birthDate;

}
