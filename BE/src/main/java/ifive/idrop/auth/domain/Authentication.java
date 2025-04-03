package ifive.idrop.auth.domain;

import ifive.idrop.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Authentication {
    @Id
    private Long userId;
    private String refreshToken;
    private String fcmToken;

    @MapsId("userId")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;

    public static Authentication of(User user) {
        Authentication authentication = new Authentication();
        authentication.user = user;
        return authentication;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
