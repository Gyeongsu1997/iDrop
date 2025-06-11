package ifive.idrop.domain.user.dto;

import ifive.idrop.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpRequest {
    private String loginId;
    private String password;
    private String name;
    private String phoneNumber;
    private String role;

    public User toEntity() {
        return User.createUser(this);
    }
}
