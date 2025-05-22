package ifive.idrop.auth.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String userId;
    private String password;
}
