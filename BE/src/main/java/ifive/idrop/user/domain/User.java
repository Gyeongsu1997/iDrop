package ifive.idrop.user.domain;

import ifive.idrop.auth.domain.Auth;
import ifive.idrop.auth.dto.LoginRequest;
import ifive.idrop.common.enums.Gender;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.parent.domain.Parent;
import ifive.idrop.user.dto.SignUpRequest;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "role", columnDefinition = "char(1)")
public abstract class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;
    private String loginId;
    private String password;
    private String name;
    protected LocalDate birthDate;
    @Column(columnDefinition = "char(1)")
    protected Gender gender;
    private String phoneNumber;
    protected String imageUrl;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Auth auth;

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
        user.auth = Auth.of(user);
        return user;
    }

    public boolean verifyUser(LoginRequest loginRequest) {
        return this.loginId.equals(loginRequest.getUserId()) && this.password.equals(loginRequest.getPassword());
    }

    public void updateRefreshToken(String refreshToken) {
        this.auth.updateRefreshToken(refreshToken);
    }

    public String getFcmToken() {
        return this.auth.getFcmToken();
    }

    public void updateFcmToken(String fcmToken) {
        this.auth.updateFcmToken(fcmToken);
    }
}
