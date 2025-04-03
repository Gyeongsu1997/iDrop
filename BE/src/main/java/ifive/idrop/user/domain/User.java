package ifive.idrop.user.domain;

import ifive.idrop.auth.dto.LoginRequest;
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
    private String refreshToken;
    private String fcmToken;

    public void setUserInfo(String loginId, String password, String name, String phoneNumber){
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public boolean verifyUser(LoginRequest loginRequest) {
        return this.loginId.equals(loginRequest.getUserId()) && this.password.equals(loginRequest.getPassword());
    }
}
