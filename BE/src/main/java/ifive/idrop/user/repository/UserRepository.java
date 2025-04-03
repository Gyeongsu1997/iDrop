package ifive.idrop.user.repository;

import ifive.idrop.driver.domain.Driver;
import ifive.idrop.entity.PickUpInfo;
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

    // todo: loginId index
    public Optional<User> findByLoginId(String loginId) {
        return em.createQuery("select u from User u where u.loginId = :loginId", User.class)
                .setParameter("loginId", loginId)
                .getResultList()
                .stream()
                .findAny();
    }

    public Optional<PickUpInfo> findPickUpInfoById(Long pickUpInfoId) {
        return Optional.ofNullable(em.find(PickUpInfo.class, pickUpInfoId));
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
