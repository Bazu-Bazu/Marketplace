package marketplace.User.Auth.Service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyUserRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String verificationCode;

}
