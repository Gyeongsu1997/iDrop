package ifive.idrop.auth.domain;

import ifive.idrop.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Authentication {
    @Id
    @Column(name = "users_id")
    private Long id;

    private String refreshToken;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static Authentication of(User user) {
        Authentication authentication = new Authentication();
        authentication.user = user;
        return authentication;
    }
}
