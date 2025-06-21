package ifive.idrop.domain.child.repository;

import ifive.idrop.domain.child.entity.Child;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChildRepository {
    private final EntityManager em;

    public void save(Child child) {
        em.persist(child);
    }

    public Optional<Child> findById(Long childId) {
        return Optional.ofNullable(em.find(Child.class, childId));
    }
}
