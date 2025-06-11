package ifive.idrop.domain.auth;

import ifive.idrop.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Auth {
    @Id
    private Long userId;
    private String refreshToken;
    private String fcmToken;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;

    public static Auth of(User user) {
        Auth auth = new Auth();
        auth.user = user;
        return auth;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
