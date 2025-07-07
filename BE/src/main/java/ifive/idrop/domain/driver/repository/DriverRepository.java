package ifive.idrop.domain.driver.repository;

import ifive.idrop.domain.driver.entity.Driver;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DriverRepository {
    private final EntityManager em;

    public Optional<Driver> findById(Long driverId) {
        return Optional.ofNullable(em.find(Driver.class, driverId));
    }

    public List<Driver> findByLocation(double startLat, double startLng, double goalLat, double goalLng) {
        String sql = "SELECT * FROM driver d JOIN users u ON d.users_id = u.users_id JOIN work_location w ON d.users_id = w.driver_id " +
                "WHERE ST_Distance_Sphere(w.point, ST_GeomFromText(:start, 4326)) <= w.radius " +
                "AND ST_Distance_Sphere(w.point, ST_GeomFromText(:goal, 4326)) <= w.radius";

        return em.createNativeQuery(sql, Driver.class)
                .setParameter("start", "POINT(" + startLat + " " + startLng + ")")
                .setParameter("goal", "POINT(" + goalLat + " " + goalLng + ")")
                .getResultList();
    }

    public List<Object[]> findAllRunningPickUpInfoOrderByreservedTimeASC(Long driverId) {
        String query = "SELECT pui, pu.reservedTime\n" +
                "FROM Subscription pui\n" +
                "JOIN PickUpHistory pu ON pui.id = pu.pickUpInfo.id\n" +
                "WHERE pui.driver.id =: driverId\n" +
                "AND FUNCTION('DATE', pu.reservedTime) = :currentDate\n"+
                "AND pu.endTime IS NULL\n" +
                "ORDER BY pu.reservedTime ASC";
        return em.createQuery(query)
                .setParameter("driverId", driverId)
                .setParameter("currentDate", LocalDate.now())
                .getResultList();
    }

    public List<Object[]> findRunningPickUpInfo(Long driverId) {
        String query = "SELECT pui, pu.reservedTime\n" +
                "FROM Subscription pui\n" +
                "JOIN PickUpHistory pu ON pui.id = pu.pickUpInfo.id\n" +
                "WHERE pui.driver.id =: driverId\n" +
                "AND pu.startTime IS NOT NULL\n" +
                "AND pu.endTime IS NULL\n" +
                "AND pu.reservedTime <= CURRENT_TIMESTAMP";

        return em.createQuery(query)
                .setParameter("driverId", driverId)
                .getResultList();
    }

    public List<Object[]> findRemainingPickUpInfo(Long driverId) {
        String query = "SELECT pui, pu.reservedTime " +
                "FROM Subscription pui " +
                "JOIN PickUpHistory pu ON pui.id = pu.pickUpInfo.id " +
                "WHERE pui.driver.id = :driverId " +
                "AND FUNCTION('DATE', pu.reservedTime) = :currentDate " +
                "AND pu.endTime IS NULL " +
                "AND pu.reservedTime > :oneHourBeforeNow";

        return em.createQuery(query, Object[].class)
                .setParameter("driverId", driverId)
                .setParameter("currentDate", LocalDate.now())
                .setParameter("oneHourBeforeNow", LocalDateTime.now().minusHours(1))
                .getResultList();
    }
}