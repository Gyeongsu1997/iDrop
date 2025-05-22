package ifive.idrop.auth.dto;

import lombok.Getter;

@Getter
public class TokenRefreshRequest {
    private String refreshToken;
}
