package ifive.idrop.user.domain;

import ifive.idrop.auth.domain.Authentication;
import ifive.idrop.auth.dto.LoginRequest;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.parent.domain.Parent;
import ifive.idrop.user.dto.SignUpRequest;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "role")
public abstract class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;

    private String loginId;
    private String password;
    private String name;
    private String phoneNumber;

    private String fcmToken;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "authentication_id")
    private Authentication authentication;

    public static User createUser(SignUpRequest signUpRequest) {
        String role = signUpRequest.getRole();
        User user;

        if ("기사".equals(role)) {
            user = new Driver();
        } else if ("부모".equals(role)) {
            user = new Parent();
        } else
            throw new BusinessException(ErrorCode.INVALID_ROLE_OF_USER);

        user.loginId = signUpRequest.getLoginId();
        user.password = signUpRequest.getPassword();
        user.name = signUpRequest.getName();
        user.phoneNumber = signUpRequest.getPhoneNumber();
        return user;
    }

    public void setUserInfo(String loginId, String password, String name, String phoneNumber){
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public boolean verifyUser(LoginRequest loginRequest) {
        return this.loginId.equals(loginRequest.getUserId()) && this.password.equals(loginRequest.getPassword());
    }

    public void updateRefreshToken(String refreshToken) {
        this.authentication.updateRefreshToken(refreshToken);
    }
}
