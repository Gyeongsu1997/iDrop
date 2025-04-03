package ifive.idrop.repository;

import ifive.idrop.driver.domain.Driver;
import ifive.idrop.parent.domain.Parent;
import ifive.idrop.user.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final EntityManager em;

    public void save(User user) {
        em.persist(user);
    }

    public Optional<User> findByUserId(String userId) {
        List<Driver> driverResultList = em.createQuery("select d from Driver d where d.userId = :userId", Driver.class)
                .setParameter("userId", userId)
                .getResultList();

        List<Parent> parentResultList = em.createQuery("select p from Parent p where p.userId = :userId", Parent.class)
                .setParameter("userId", userId)
                .getResultList();

        List<User> result = Stream.concat(driverResultList.stream(), parentResultList.stream()).toList();

        return result.stream().findAny();
    }

    public Optional<User> findByRefreshToken(String refreshToken) {
        List<Driver> driverResultList = em.createQuery("select d from Driver d where d.refreshToken = :refreshToken", Driver.class)
                .setParameter("refreshToken", refreshToken)
                .getResultList();

        List<Parent> parentResultList = em.createQuery("select p from Parent p where p.refreshToken = :refreshToken", Parent.class)
                .setParameter("refreshToken", refreshToken)
                .getResultList();

        List<User> result = Stream.concat(driverResultList.stream(), parentResultList.stream()).toList();

        return result.stream().findAny();
    }
}
