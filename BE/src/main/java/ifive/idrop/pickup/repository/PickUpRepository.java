package ifive.idrop.pickup.repository;

import ifive.idrop.subscription.domain.SubscriptionStatus;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.pickup.domain.PickUpHistory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import ifive.idrop.subscription.domain.Subscription;
import ifive.idrop.pickup.domain.PickUpLocation;

@Repository
@RequiredArgsConstructor
public class PickUpRepository {
    private final EntityManager em;

    public Optional<PickUpHistory> findPickUpById(Long pickUpId) {
        return Optional.ofNullable(em.find(PickUpHistory.class, pickUpId));
    }

    public Optional<Subscription> findPickUpInfoById(Long pickUpInfoId) {
        return Optional.ofNullable(em.find(Subscription.class, pickUpInfoId));
    }

    public List<PickUpHistory> findReservedPickUpsByDriver(Long driverId) {
        TypedQuery<PickUpHistory> query = em.createQuery(
                "SELECT p FROM PickUpHistory p " +
                        "JOIN p.pickUpInfo pi " +
                        "JOIN pi.pickUpSubscribe ps " +
                        "JOIN pi.driver d " +
                        "WHERE ps.status = :status " +
                        "AND d.id = :driverId " +
                        "AND p.reservedTime >= CURRENT_TIMESTAMP", PickUpHistory.class);
        query.setParameter("status", SubscriptionStatus.PROGRESS)
                .setParameter("driverId", driverId);
        return query.getResultList();
    }

    public void savePickUp(PickUpHistory pick) {
        em.persist(pick);
    }

    public List<PickUpHistory> findPickUpByPickUpInfoIdAndParentIdOrderByReservedTime(Long parentId, Long pickInfoId) {
        String query = "SELECT p FROM PickUpHistory p\n" +
                "WHERE p.pickUpInfo.id =: pickInfoId\n" +
                "AND p.pickUpInfo.child.parent.id =: parentId\n" +
                "AND p.startTime IS NOT NULL\n" +
                "ORDER BY p.reservedTime DESC";
        return em.createQuery(query, PickUpHistory.class)
                .setParameter("pickInfoId", pickInfoId)
                .setParameter("parentId", parentId)
                .getResultList();
    }

    public Optional<PickUpHistory> findById(Long pickUpId) {
        return Optional.ofNullable(em.find(PickUpHistory.class, pickUpId));
    }

    public List<Subscription> findWaitingPickUpInfoByDriverId(Long driverId) {
        TypedQuery<Subscription> query = em.createQuery(
                "SELECT pui FROM Subscription pui " +
                        "JOIN pui.pickUpSubscribe ps " +
                        "JOIN pui.driver d " +
                        "WHERE d.id = :driverId " +
                        "AND ps.status = :status", Subscription.class);
        query.setParameter("driverId", driverId)
                .setParameter("status", SubscriptionStatus.REQUEST);
        return query.getResultList();
    }

    public List<Subscription> findPickUpInfoByParentIdInTheLatestOrder(Long parentId) {
        TypedQuery<Subscription> query = em.createQuery(
                "SELECT pui FROM Subscription pui " +
                        "JOIN pui.pickUpSubscribe ps " +
                        "JOIN pui.child c " +
                        "JOIN c.parent p " +
                        "WHERE p.id = :parentId " +
                        "ORDER BY CASE WHEN ps.expiredDate IS NULL THEN 1 ELSE 0 END, " +
                        "ps.expiredDate DESC", Subscription.class);
        query.setParameter("parentId", parentId);
        return query.getResultList();
    }

    public List<Subscription> findPickUpInfoByDriverIdTheLatestOrder(Long driverId) {
        TypedQuery<Subscription> query = em.createQuery(
                "SELECT pui FROM Subscription pui " +
                        "JOIN pui.pickUpSubscribe ps " +
                        "JOIN pui.driver d " +
                        "WHERE d.id = :driverId " +
                        "AND ps.status <> :cancel " +
                        "AND ps.status <> :decline " +
                        "ORDER BY ps.requestDate DESC", Subscription.class);
        query.setParameter("driverId", driverId)
                .setParameter("cancel", SubscriptionStatus.CANCELED)
                .setParameter("decline", SubscriptionStatus.REJECTED);
        return query.getResultList();
    }

    public void savePickUpStartInfo(Long pickupId, String startImage, String startMessage) {
        PickUpHistory pickUpHistory = Optional.ofNullable(em.find(PickUpHistory.class, pickupId))
                .orElseThrow(() -> new BusinessException(ErrorCode.PICKUP_NOT_FOUND));

        pickUpHistory.startPickUp(startImage, startMessage);
        em.merge(pickUpHistory);
    }

    public void savePickUpEndInfo(Long pickupId, String endImage, String endMessage) {
        PickUpHistory pickUpHistory = Optional.ofNullable(em.find(PickUpHistory.class, pickupId))
                .orElseThrow(() -> new BusinessException(ErrorCode.PICKUP_NOT_FOUND));

        pickUpHistory.endPickUp(endImage, endMessage);
        em.merge(pickUpHistory);
    }

    /**
     * driverId로 현재 해당 기사의 업무 시간에 해당하는 PickUp들 찾기
     * @param driverId
     * @return List<PickUp>
     */
    public List<PickUpHistory> findPickUpsByDriverIdWithCurrentTimeInReservedRange(Long driverId) {
        LocalDateTime now = LocalDateTime.now();

        // 현재 시간이 reservedTime ~ reservedTime+1시간에 해당하는 PickUp들 찾기
        String jpql = "SELECT p FROM PickUpHistory p WHERE p.pickUpInfo.driver.id = :driverId " +
                "AND (p.reservedTime - 10 MINUTE) <= :now AND :now <= (p.reservedTime + 1 HOUR)";

        TypedQuery<PickUpHistory> query = em.createQuery(jpql, PickUpHistory.class);
        query.setParameter("driverId", driverId);
        query.setParameter("now", now);

        return query.getResultList();
    }

    public Long getCurrentPickUpSize(Long parentId) {
        LocalDateTime now = LocalDateTime.now();

        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(p) FROM PickUpHistory p " +
                        "JOIN p.pickUpInfo pi " +
                        "JOIN pi.child c " +
                        "WHERE c.parent.id = :parentId " +
                        "AND p.reservedTime <= :now " +
                        "AND (p.reservedTime + 1 HOUR) > :now", Long.class);

        query.setParameter("parentId", parentId);
        query.setParameter("now", now);

        return query.getSingleResult();
    }

    public Optional<PickUpLocation> findPickUpLocationById(Long id) {
        PickUpLocation pickUpLocation = em.find(PickUpLocation.class, id);
        return Optional.ofNullable(pickUpLocation);
    }

    /**
     * driverId로 현재 해당 기사의 업무 시간에 해당하는 PickUp 찾기
     * @param driverId
     * @return PickUp
     */
    public Optional<PickUpHistory> findPickUpByDriverIdWithCurrentTimeInReservedWindow(Long driverId) {
        LocalDateTime now = LocalDateTime.now();

        //현재 시간이 reservedTime ~ reservedTime+1시간 에 해당하는 PickUp 찾기
        String jpql = "SELECT p FROM PickUpHistory p WHERE p.pickUpInfo.driver.id = :driverId " +
                "AND (p.reservedTime) <= :now AND :now < (p.reservedTime + 1 HOUR)";

        TypedQuery<PickUpHistory> query = em.createQuery(jpql, PickUpHistory.class);
        query.setParameter("driverId", driverId);
        query.setParameter("now", now);

        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * pickUpId로 해당 픽업의 child id, parent id 찾기
     * @param pickUpId
     * @return Object[]  [0]: childId, [1]: parentId
     */
    public Object[] findChildAndParentIdByPickUp(Long pickUpId) {
        String jpql = "SELECT c.id, p.id FROM PickUpHistory pu " +
                "JOIN pu.pickUpInfo.child c " +
                "JOIN c.parent p " +
                "WHERE pu.id = :pickUpId";

        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        query.setParameter("pickUpId", pickUpId);

        return query.getSingleResult();
    }
}
