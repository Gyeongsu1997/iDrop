package ifive.idrop.domain.auth.repository;

import ifive.idrop.domain.auth.entity.Auth;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthRepository {
    private final EntityManager em;

    // todo: refreshToken 인덱스
    public Optional<Auth> findByRefreshToken(String refreshToken) {
        return em.createQuery("select a from Auth a where a.refreshToken = :refreshToken", Auth.class)
                .setParameter("refreshToken", refreshToken)
                .getResultList()
                .stream()
                .findAny();
    }
}
