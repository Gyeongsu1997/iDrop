package ifive.idrop.user.dto;

import ifive.idrop.driver.domain.Driver;
import ifive.idrop.parent.domain.Parent;
import ifive.idrop.user.domain.User;
import ifive.idrop.entity.enums.Role;
import ifive.idrop.common.exception.CommonException;
import ifive.idrop.common.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpRequest {
    private String userId;
    private String password;
    private String name;
    private String phone;
    private String role;

    public User toEntity() {
        if ("기사".equals(role)) {
            Driver driver = new Driver();
            driver.setUserInfo(userId, password, name, phone);
            return driver;
        } else if ("부모".equals(role)) {
            Parent parent = new Parent();
            parent.setUserInfo(userId, password, name, phone);
            return parent;
        } else
            throw new CommonException(ErrorCode.INVALID_ROLE_OF_USER);
    }
}
