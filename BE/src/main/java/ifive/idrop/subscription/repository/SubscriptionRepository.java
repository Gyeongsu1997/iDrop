package ifive.idrop.subscription.repository;

import ifive.idrop.subscription.domain.Subscription;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepository {
    private final EntityManager em;

    public void save(Subscription subscription) {
        em.persist(subscription);
    }
}
