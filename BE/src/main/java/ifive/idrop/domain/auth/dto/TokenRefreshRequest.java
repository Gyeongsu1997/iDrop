package ifive.idrop.domain.auth.dto;

import lombok.Getter;

@Getter
public class TokenRefreshRequest {
    private String refreshToken;
}
