package ifive.idrop.auth.repository;

import ifive.idrop.auth.domain.Auth;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthRepository {
    private final EntityManager em;

    public Optional<Auth> findByRefreshToken(String refreshToken) {
        return em.createQuery("select a from Auth a where a.refreshToken = :refreshToken", Auth.class)
                .setParameter("refreshToken", refreshToken)
                .getResultList()
                .stream()
                .findAny();
    }
}
