package ifive.idrop.domain.child.repository;

import ifive.idrop.domain.child.entity.Child;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    public List<Child> findByParentId(Long parentId) {
        return em.createQuery("SELECT c FROM Child c where c.parent.id =: parentId", Child.class)
                .setParameter("parentId", parentId)
                .getResultList();
    }
}
