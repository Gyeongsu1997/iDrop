package ifive.idrop.subscription.repository;

import ifive.idrop.subscription.domain.Subscription;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepository {
    private final EntityManager em;

    public void save(Subscription subscription) {
        em.persist(subscription);
    }

    public List<Subscription> findByDriverId(Long driverId) {
        return em.createQuery("select s from Subscription s where s.driver.id = :driverId order by s.requestDate desc", Subscription.class)
                .setParameter("driverId", driverId)
                .getResultList();
    }
}
